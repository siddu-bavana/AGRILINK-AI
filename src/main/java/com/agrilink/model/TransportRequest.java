package com.agrilink.model;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name="transport_requests")
public class TransportRequest {
    public enum Status { OPEN, MATCHED, COMPLETED, CANCELLED }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long farmerId;
    public String cropName;
    public Double quantityKg;
    public String pickupVillage;
    public String deliveryMarket;
    public LocalDate harvestDate;
    @Enumerated(EnumType.STRING) public Status status = Status.OPEN;
    public LocalDateTime createdAt = LocalDateTime.now();
}
