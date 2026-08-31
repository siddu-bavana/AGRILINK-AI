package com.agrilink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="analysis_history")
public class AnalysisRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long userId;
    @Column(nullable=false) public String serviceType;
    @Column(columnDefinition="TEXT") public String requestJson;
    @Column(columnDefinition="TEXT") public String resultJson;
    public LocalDateTime createdAt = LocalDateTime.now();
}

