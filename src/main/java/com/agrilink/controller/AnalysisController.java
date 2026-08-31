package com.agrilink.controller;

import com.agrilink.model.AnalysisRecord;
import com.agrilink.repository.AnalysisRecordRepository;
import com.agrilink.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class AnalysisController {
    private final AnalysisService service;
    private final AnalysisRecordRepository history;

    AnalysisController(AnalysisService s, AnalysisRecordRepository h) {
        this.service = s;
        this.history = h;
    }

    @PostMapping("/{type}/analyze")
    public ResponseEntity<?> analyze(@PathVariable int type, @RequestBody AnalysisService.Request r, Authentication a) {
        if (a == null || a.getDetails() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please Sign Out and Sign In again."));
        }
        try {
            Long userId = (Long) a.getDetails();
            AnalysisService.Response resp = service.analyze(type, r, userId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() == null ? "Analysis failed" : e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(Authentication a) {
        if (a == null || a.getDetails() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please Sign Out and Sign In again."));
        }
        Long userId = (Long) a.getDetails();
        List<AnalysisRecord> records = history.findTop20ByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(records);
    }
}

