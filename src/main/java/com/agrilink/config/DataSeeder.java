package com.agrilink.config;

import com.agrilink.model.*; import com.agrilink.repository.*; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(BuyerRepository buyers){return args->{if(buyers.count()>0)return;buyers.save(make("ABC Foods","Ongole","Tomato","A",30,5000,96,148,98));buyers.save(make("FreshMart","Prakasam","Tomato","A",28,3000,91,102,94));buyers.save(make("XYZ Processing","Guntur","Tomato","B",27,8000,87,76,90));buyers.save(make("Green Basket","Hyderabad","Paddy","A",25,10000,89,65,92));};}
    private Buyer make(String company,String district,String crop,String grade,double price,double capacity,int score,int deals,double payment){Buyer b=new Buyer();b.name=company;b.companyName=company;b.mobile="90000000"+(score%100);b.district=district;b.preferredCrop=crop;b.preferredGrade=grade;b.maxPricePerKg=price;b.capacityKg=capacity;b.reliabilityScore=score;b.completedDeals=deals;b.onTimePaymentPercent=payment;return b;}
}
