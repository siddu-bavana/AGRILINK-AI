package com.agrilink.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="users", uniqueConstraints={@UniqueConstraint(columnNames="mobile"), @UniqueConstraint(columnNames="email")})
public class User {
    public enum Role { FARMER, BUYER, ADMIN }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String name;
    @Column(nullable=false) public String mobile;
    public String email;
    @JsonIgnore public String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role = Role.FARMER;
    public String language = "en";
    public String district;
    public Double latitude;
    public Double longitude;
    @Column(length=500) public String detectedLocation;
    public boolean verified;
    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime lastLoginAt = LocalDateTime.now();
    public Integer loginCount = 0;
}
