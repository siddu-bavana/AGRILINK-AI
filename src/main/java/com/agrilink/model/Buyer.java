package com.agrilink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="buyers")
public class Buyer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String name;
    public String companyName;
    @Column(nullable=false) public String mobile;
    @Column(nullable=false) public String district;
    public String preferredCrop;
    public String preferredGrade;
    public Double maxPricePerKg;
    public Double capacityKg;
    public int reliabilityScore = 80;
    public int completedDeals;
    public int cancelledDeals;
    public double onTimePaymentPercent = 90;
    public boolean verified = true;
    public LocalDateTime createdAt = LocalDateTime.now();
}
