package com.agrilink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="rescue_requests")
public class RescueRequest {
    public enum Status { OPEN, BUYERS_ALERTED, OFFER_ACCEPTED, CLOSED }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long farmerId;
    public String cropName;
    public Double availableQuantityKg;
    @Column(name="minimum_price_per_100kg_bag") public Double minimumPricePer100kgBag;
    public String freshnessRemaining;
    public String pickupVillage;
    @Enumerated(EnumType.STRING) public Status status = Status.OPEN;
    public LocalDateTime createdAt = LocalDateTime.now();
}
