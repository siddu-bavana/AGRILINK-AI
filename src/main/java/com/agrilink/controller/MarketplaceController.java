package com.agrilink.controller;

import com.agrilink.model.*; import com.agrilink.repository.*;
import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api")
public class MarketplaceController {
    private final BuyerRepository buyers; private final CropListingRepository crops; private final TransportRequestRepository transport; private final RescueRequestRepository rescue;
    private Long uid(Authentication a){
        if (a == null || a.getDetails() == null) throw new SecurityException("Session expired. Please Sign Out and Sign In again.");
        return (Long)a.getDetails();
    }

    @GetMapping("/public/buyers") List<Buyer> allBuyers(){return buyers.findAll();}
    @GetMapping("/buyers/{id}") Buyer buyer(@PathVariable Long id){return buyers.findById(id).orElseThrow();}
    @PostMapping("/buyers") ResponseEntity<Buyer> addBuyer(@RequestBody Buyer b){return ResponseEntity.status(201).body(buyers.save(b));}
    @PutMapping("/buyers/{id}") Buyer updateBuyer(@PathVariable Long id,@RequestBody Buyer b){b.id=id;return buyers.save(b);}
    @DeleteMapping("/buyers/{id}") void deleteBuyer(@PathVariable Long id){buyers.deleteById(id);}
    @GetMapping("/public/crops") List<CropListing> allCrops(){return crops.findAll();}
    @GetMapping("/crops/mine") List<CropListing> myCrops(Authentication a){return crops.findByFarmerId(uid(a));}
    @PostMapping("/crops") ResponseEntity<CropListing> addCrop(Authentication a,@RequestBody CropListing c){c.id=null;c.farmerId=uid(a);return ResponseEntity.status(201).body(crops.save(c));}
    @PutMapping("/crops/{id}") CropListing updateCrop(Authentication a,@PathVariable Long id,@RequestBody CropListing c){var old=crops.findById(id).orElseThrow();if(!old.farmerId.equals(uid(a)))throw new SecurityException("Not your listing");c.id=id;c.farmerId=old.farmerId;return crops.save(c);}
    @DeleteMapping("/crops/{id}") void deleteCrop(Authentication a,@PathVariable Long id){var c=crops.findById(id).orElseThrow();if(!c.farmerId.equals(uid(a)))throw new SecurityException("Not your listing");crops.delete(c);}
    @GetMapping("/transport") List<TransportRequest> transports(){return transport.findAll();}
    @PostMapping("/transport") ResponseEntity<TransportRequest> addTransport(Authentication a,@RequestBody TransportRequest t){t.id=null;t.farmerId=uid(a);return ResponseEntity.status(201).body(transport.save(t));}
    @PutMapping("/transport/{id}") TransportRequest updateTransport(@PathVariable Long id,@RequestBody TransportRequest t){t.id=id;return transport.save(t);}
    @DeleteMapping("/transport/{id}") void deleteTransport(@PathVariable Long id){transport.deleteById(id);}
    @GetMapping("/rescue") List<RescueRequest> rescues(){return rescue.findAll();}
    @PostMapping("/rescue") ResponseEntity<RescueRequest> addRescue(Authentication a,@RequestBody RescueRequest r){r.id=null;r.farmerId=uid(a);return ResponseEntity.status(201).body(rescue.save(r));}
    @PutMapping("/rescue/{id}") RescueRequest updateRescue(@PathVariable Long id,@RequestBody RescueRequest r){r.id=id;return rescue.save(r);}
    @DeleteMapping("/rescue/{id}") void deleteRescue(@PathVariable Long id){rescue.deleteById(id);}
}
