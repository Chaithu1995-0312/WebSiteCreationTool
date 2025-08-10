package com.example.info.controller;

import com.example.info.model.GeneratedContent;
import com.example.info.service.ContentGenerationService;
import com.example.info.repository.GeneratedContentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "http://localhost:3000")
public class ContentGenerationController {

    private final ContentGenerationService contentGenerationService;
    private final GeneratedContentRepository contentRepository;

    public ContentGenerationController(
            ContentGenerationService contentGenerationService,
            GeneratedContentRepository contentRepository) {
        this.contentGenerationService = contentGenerationService;
        this.contentRepository = contentRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateContent(@RequestBody ContentGenerationRequest request) {
        try {
            GeneratedContent content = contentGenerationService.generateContent(request.getTopic());
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content generation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/regenerate/{id}")
    public ResponseEntity<?> regenerateContent(@PathVariable Long id, @RequestBody ContentGenerationRequest request) {
        try {
            GeneratedContent content = contentGenerationService.regenerateContent(id, request.getTopic());
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content regeneration failed: " + e.getMessage()));
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editContent(@PathVariable Long id, @RequestBody EditContentRequest request) {
        try {
            Optional<GeneratedContent> optionalContent = contentRepository.findById(id);
            if (optionalContent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            GeneratedContent content = optionalContent.get();
            content.setTitle(request.getTitle());
            content.setArticleText(request.getArticleText());
            content.setSummary(request.getSummary());
            content.setStatus("EDITED");

            GeneratedContent savedContent = contentRepository.save(content);
            return ResponseEntity.ok(savedContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content editing failed: " + e.getMessage()));
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveContent(@PathVariable Long id) {
        try {
            Optional<GeneratedContent> optionalContent = contentRepository.findById(id);
            if (optionalContent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            GeneratedContent content = optionalContent.get();
            content.setStatus("APPROVED");

            GeneratedContent savedContent = contentRepository.save(content);
            return ResponseEntity.ok(savedContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Content approval failed: " + e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<GeneratedContent>> getContentHistory() {
        List<GeneratedContent> history = contentRepository.findAll();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        Optional<GeneratedContent> content = contentRepository.findById(id);
        if (content.isPresent()) {
            return ResponseEntity.ok(content.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Request/Response DTOs
    public static class ContentGenerationRequest {
        private String topic;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }

    public static class EditContentRequest {
        private String title;
        private String articleText;
        private String summary;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getArticleText() { return articleText; }
        public void setArticleText(String articleText) { this.articleText = articleText; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}