package com.example.info.controller;

import com.example.info.model.DiagramRequest;
import com.example.info.model.DiagramResponse;
import com.example.info.service.DiagramGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/diagrams")
@CrossOrigin(origins = "http://localhost:3000")
public class DiagramController {

    private final DiagramGenerationService diagramService;

    public DiagramController(DiagramGenerationService diagramService) {
        this.diagramService = diagramService;
    }

    @PostMapping("/generate")
    public ResponseEntity<DiagramResponse> generateDiagram(@RequestBody DiagramRequest request) {
        try {
            DiagramResponse response = diagramService.generateDiagram(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/generate-async")
    public ResponseEntity<String> generateDiagramAsync(@RequestBody DiagramRequest request) {
        CompletableFuture<DiagramResponse> future = CompletableFuture.supplyAsync(() ->
                diagramService.generateDiagram(request)
        );

        // In production, you'd store this in Redis with a job ID and return the job ID
        return ResponseEntity.accepted().body("Processing diagram generation...");
    }

    @GetMapping("/types")
    public ResponseEntity<String[]> getSupportedTypes() {
        return ResponseEntity.ok(new String[]{
                "architecture", "flowchart", "timeline", "conceptual", "professional"
        });
    }
}