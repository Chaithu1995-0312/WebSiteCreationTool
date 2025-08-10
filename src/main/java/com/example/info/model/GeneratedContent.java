package com.example.info.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class GeneratedContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String title;
    @Column(length = 10000)
    private String articleText;
    @Column(length = 2000)
    private String summary;

    @ElementCollection
    private List<String> diagramIdeas;

    @ElementCollection
    private List<String> keyInsights;

    @ElementCollection
    private List<String> relatedTopics;

    @ElementCollection
    private List<String> sourceUrls;

    private String status; // GENERATED, EDITED, APPROVED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GeneratedContent() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArticleText() { return articleText; }
    public void setArticleText(String articleText) { this.articleText = articleText; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getDiagramIdeas() { return diagramIdeas; }
    public void setDiagramIdeas(List<String> diagramIdeas) { this.diagramIdeas = diagramIdeas; }
    public List<String> getKeyInsights() { return keyInsights; }
    public void setKeyInsights(List<String> keyInsights) { this.keyInsights = keyInsights; }
    public List<String> getRelatedTopics() { return relatedTopics; }
    public void setRelatedTopics(List<String> relatedTopics) { this.relatedTopics = relatedTopics; }
    public List<String> getSourceUrls() { return sourceUrls; }
    public void setSourceUrls(List<String> sourceUrls) { this.sourceUrls = sourceUrls; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
