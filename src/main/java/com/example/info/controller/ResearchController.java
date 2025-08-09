package com.example.info.controller;

import com.example.info.model.ResearchResult;
import com.example.info.service.ResearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
public class ResearchController {
    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @GetMapping
    public ResponseEntity<?> research(@RequestParam String topic) {
        try {
            ResearchResult result = researchService.researchTopic(topic);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}