package com.agrilink.config;

import com.agrilink.model.*;
import com.agrilink.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(BuyerRepository buyers, CropListingRepository crops, TransportRequestRepository transport) {
        return args -> {
            if (buyers.count() == 0) {
                buyers.save(make("ABC Foods & Processing", "Ongole", "Tomato", "A", 32, 10000, 96, 148, 98));
                buyers.save(make("FreshMart Agro", "Prakasam", "Tomato", "A", 30, 5000, 93, 112, 95));
                buyers.save(make("XYZ Spice & Processing", "Guntur", "Chilli", "A", 180, 15000, 91, 194, 96));
                buyers.save(make("Green Basket Logistics", "Hyderabad", "Paddy", "A", 26, 25000, 89, 88, 92));
                buyers.save(make("Sri Lakshmi Cotton Traders", "Kurnool", "Cotton", "A", 75, 20000, 94, 160, 97));
                buyers.save(make("Kakinada Export Hub", "Kakinada", "Tomato", "A", 31, 12000, 95, 135, 96));
                buyers.save(make("Vijaya Organics", "Vijayawada", "Vegetables", "A", 35, 8000, 90, 79, 91));
                buyers.save(make("Royal Rice Industries", "East Godavari", "Paddy", "A", 28, 30000, 97, 210, 99));
            }
            if (crops.count() == 0) {
                CropListing c1 = new CropListing();
                c1.cropName = "Tomato"; c1.quantityKg = 15000.0; c1.pricePer100kgBag = 2500.0; c1.district = "Guntur"; c1.status = CropListing.Status.AVAILABLE;
                crops.save(c1);
                CropListing c2 = new CropListing();
                c2.cropName = "Paddy"; c2.quantityKg = 45000.0; c2.pricePer100kgBag = 2200.0; c2.district = "Kakinada"; c2.status = CropListing.Status.AVAILABLE;
                crops.save(c2);
            }
            if (transport.count() == 0) {
                TransportRequest t1 = new TransportRequest();
                t1.cropName = "Tomato"; t1.quantityKg = 1500.0; t1.pickupVillage = "Surampalem"; t1.deliveryMarket = "Guntur Market"; t1.status = TransportRequest.Status.OPEN;
                transport.save(t1);
                TransportRequest t2 = new TransportRequest();
                t2.cropName = "Paddy"; t2.quantityKg = 3000.0; t2.pickupVillage = "Peddapuram"; t2.deliveryMarket = "Kakinada Market"; t2.status = TransportRequest.Status.OPEN;
                transport.save(t2);
            }
        };
    }

    private Buyer make(String company, String district, String crop, String grade, double price, double capacity, int score, int deals, double payment) {
        Buyer b = new Buyer();
        b.name = company;
        b.companyName = company;
        b.mobile = "98480" + String.format("%05d", (int)(Math.random() * 100000));
        b.district = district;
        b.preferredCrop = crop;
        b.preferredGrade = grade;
        b.maxPricePerKg = price;
        b.capacityKg = capacity;
        b.reliabilityScore = score;
        b.completedDeals = deals;
        b.onTimePaymentPercent = payment;
        b.verified = true;
        return b;
    }
}

