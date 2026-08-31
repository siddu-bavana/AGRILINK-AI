package com.agrilink.controller;

import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HealthController {
    @GetMapping("/health")
    public Map<String,Object> health(){return Map.of("status","UP","application","AgriLink AI","time",Instant.now().toString());}
}
