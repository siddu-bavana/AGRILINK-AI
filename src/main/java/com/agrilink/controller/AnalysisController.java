package com.agrilink.controller;

import com.agrilink.model.AnalysisRecord; import com.agrilink.repository.AnalysisRecordRepository; import com.agrilink.service.AnalysisService;
import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api/services")
public class AnalysisController {
    private final AnalysisService service; private final AnalysisRecordRepository history;
    AnalysisController(AnalysisService s,AnalysisRecordRepository h){service=s;history=h;}
    @PostMapping("/{type}/analyze") AnalysisService.Response analyze(@PathVariable int type,@RequestBody AnalysisService.Request r,Authentication a)throws Exception{return service.analyze(type,r,(Long)a.getDetails());}
    @GetMapping("/history") List<AnalysisRecord> history(Authentication a){return history.findTop20ByUserIdOrderByCreatedAtDesc((Long)a.getDetails());}
}
