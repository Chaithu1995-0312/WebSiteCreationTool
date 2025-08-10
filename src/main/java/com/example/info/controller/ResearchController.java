package com.example.info.controller;

import com.example.info.model.ResearchQuery;
import com.example.info.service.ResearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "http://localhost:3000")
public class ResearchController {

    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/query")
    public ResponseEntity<?> conductResearch(@RequestBody ResearchRequest request) {
        try {
            ResearchQuery result = researchService.conductResearch(request.getTopic());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Research failed: " + e.getMessage()));
        }
    }

    public static class ResearchRequest {
        private String topic;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}