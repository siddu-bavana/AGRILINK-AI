package com.agrilink.service;

import com.agrilink.model.*; import com.agrilink.repository.*; import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.*; import java.time.format.DateTimeFormatter; import java.time.temporal.ChronoUnit; import java.util.*;

@Service
public class AnalysisService {
    private final BuyerRepository buyers; private final CropListingRepository crops; private final TransportRequestRepository transports; private final RescueRequestRepository rescues; private final AnalysisRecordRepository history; private final ObjectMapper json;
    public AnalysisService(BuyerRepository b,CropListingRepository c,TransportRequestRepository t,RescueRequestRepository r,AnalysisRecordRepository h,ObjectMapper j){buyers=b;crops=c;transports=t;rescues=r;history=h;json=j;}
    public record Request(List<String> values,String language){}
    public record Response(List<String> results,String message,Long recordId){}
    private String v(List<String>x,int i){return x!=null&&x.size()>i?x.get(i).trim():"";}
    private double n(String x){try{return Double.parseDouble(x.replaceAll("[^0-9.]",""));}catch(Exception e){return 0;}}
    private String money(double x){return "₹"+String.format(Locale.US,"%,.0f",x);}

    @Transactional public Response analyze(int type, Request req, Long userId) throws Exception {
        List<String> v=req.values()==null?List.of():req.values(); List<String> out;
        out=switch(type){
            case 0 -> sharedTransport(v,userId);
            case 1 -> buyerReliability(v);
            case 2 -> profit(v);
            case 3 -> rescue(v,userId);
            case 4 -> oversupply(v);
            case 5 -> harvest(v);
            case 6 -> cropDetails(v);
            case 7 -> buyerMatching(v);
            default -> throw new IllegalArgumentException("Unknown service: "+type);
        };
        AnalysisRecord h=new AnalysisRecord();h.userId=userId;h.serviceType=String.valueOf(type);h.requestJson=json.writeValueAsString(req);h.resultJson=json.writeValueAsString(out);history.save(h);
        return new Response(out,"Analysis completed using database data",h.id);
    }
    private List<String> sharedTransport(List<String> v,Long uid){
        String market=v(v,3);var open=transports.findByStatusAndDeliveryMarketIgnoreCase(TransportRequest.Status.OPEN,market);
        double totalKg=n(v(v,1))+open.stream().mapToDouble(x->x.quantityKg==null?0:x.quantityKg).sum();int nearby=open.size();double fullCost=4000;double share=fullCost/Math.max(1,nearby+1);
        TransportRequest t=new TransportRequest();t.farmerId=uid;t.cropName=v(v,0);t.quantityKg=n(v(v,1));t.pickupVillage=v(v,2);t.deliveryMarket=market;try{t.harvestDate=LocalDate.parse(v(v,4));}catch(Exception ignored){}transports.save(t);
        return List.of(nearby+" nearby requests ("+(int)totalKg+" kg)",money(share),money(fullCost-share));
    }
    private List<String> buyerReliability(List<String> v){
        String name=v(v,0); Buyer b=buyers.findAll().stream().filter(x->x.name.equalsIgnoreCase(name)||(x.companyName!=null&&x.companyName.equalsIgnoreCase(name))).findFirst().orElse(null);
        return b==null?List.of("Buyer not found","0%","0"):List.of(b.reliabilityScore+" / 100",String.format("%.0f%%",b.onTimePaymentPercent),String.valueOf(b.completedDeals));
    }
    private List<String> profit(List<String> v){double yieldBags=n(v(v,1)),pricePerBag=n(v(v,2)),cost=n(v(v,3));double revenue=yieldBags*pricePerBag;return List.of(money(revenue),money(cost),money(revenue-cost));}
    private List<String> rescue(List<String> v,Long uid){
        String crop=v(v,0);double qty=n(v(v,1)),min=n(v(v,2));var matches=buyers.findByVerifiedTrue().stream().filter(b->b.preferredCrop==null||b.preferredCrop.equalsIgnoreCase(crop)).filter(b->b.maxPricePerKg==null||b.maxPricePerKg*100>=min).toList();
        RescueRequest r=new RescueRequest();r.farmerId=uid;r.cropName=crop;r.availableQuantityKg=qty;r.minimumPricePer100kgBag=min;r.freshnessRemaining=v(v,3);r.pickupVillage=v(v,4);r.status=RescueRequest.Status.BUYERS_ALERTED;rescues.save(r);
        double offer=matches.stream().map(b->b.maxPricePerKg).filter(Objects::nonNull).mapToDouble(Double::doubleValue).max().orElse(min/100)*100;return List.of(matches.size()+" verified buyers",money(offer)+" / 100 kg bag","Request #"+r.id+" is live");
    }
    private List<String> oversupply(List<String> v){String crop=v(v,0);double planned=n(v(v,1));var listed=crops.findByCropNameIgnoreCaseAndStatus(crop,CropListing.Status.AVAILABLE);double supply=listed.stream().mapToDouble(x->x.quantityKg==null?0:x.quantityKg).sum();String risk=supply>50000?"High":supply>15000?"Moderate":"Low";String alt=crop.equalsIgnoreCase("tomato")?"Green gram":"Millets";return List.of(risk,String.format("%.1f acres planned; %.0f kg listed",planned,supply),alt);}
    private List<String> harvest(List<String> v){String crop=v(v,0).toLowerCase();int days=crop.contains("tomato")?110:crop.contains("paddy")||crop.contains("rice")?135:crop.contains("cotton")?170:120;LocalDate sow;try{sow=LocalDate.parse(v(v,1));}catch(Exception e){sow=LocalDate.now().minusDays(days-10);}LocalDate date=sow.plusDays(days);long left=ChronoUnit.DAYS.between(LocalDate.now(),date);String maturity=left<=0?"Ready":Math.max(0,100-left*100/days)+"%";return List.of(date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),maturity,left<=7?"High":"Moderate");}
    private List<String> cropDetails(List<String> v){String c=v(v,0).toLowerCase();double acres=Math.max(1,n(v(v,1)));int days=c.contains("tomato")?110:c.contains("paddy")||c.contains("rice")?135:c.contains("cotton")?170:120;double investment=(c.contains("tomato")?65000:c.contains("paddy")?42000:50000)*acres;double profit=investment*(c.contains("tomato")?1.25:.75);return List.of(days+" days",money(investment),money(profit));}
    private List<String> buyerMatching(List<String> v){String crop=v(v,0),grade=v(v,2),place=v(v,3);double qty=n(v(v,1));record M(Buyer b,int score){}List<M> m=buyers.findByVerifiedTrue().stream().map(b->{int s=20;s+=b.preferredCrop!=null&&b.preferredCrop.equalsIgnoreCase(crop)?30:0;s+=b.capacityKg==null||b.capacityKg>=qty?20:5;s+=b.preferredGrade==null||b.preferredGrade.equalsIgnoreCase(grade)?15:0;s+=b.district.equalsIgnoreCase(place)?15:5;return new M(b,Math.min(99,s));}).sorted((a,b)->Integer.compare(b.score,a.score)).limit(3).toList();List<String> out=new ArrayList<>();for(M x:m)out.add((x.b.companyName==null?x.b.name:x.b.companyName)+" — "+x.score+"%");while(out.size()<3)out.add("No additional verified buyer");return out;}
}
