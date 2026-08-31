package com.agrilink.model;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name="crop_listings")
public class CropListing {
    public enum Status { AVAILABLE, MATCHED, SOLD, CANCELLED }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long farmerId;
    @Column(nullable=false) public String cropName;
    @Column(nullable=false) public Double quantityKg;
    public String grade;
    public Double pricePerKg;
    @Column(nullable=false) public String location;
    public LocalDate harvestDate;
    public String imageUrl;
    @Enumerated(EnumType.STRING) public Status status = Status.AVAILABLE;
    public LocalDateTime createdAt = LocalDateTime.now();
}
