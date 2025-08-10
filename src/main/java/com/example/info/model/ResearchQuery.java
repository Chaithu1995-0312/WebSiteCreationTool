package com.example.info.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ResearchQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String title;
    @Column(length = 5000)
    private String comprehensiveResponse;
    @Column(length = 2000)
    private String summary;

    @ElementCollection
    private List<String> keyPoints;

    @ElementCollection
    private List<String> sourceUrls;

    // Add these missing fields for Research mode
    @ElementCollection
    private List<String> diagramIdeas;

    @ElementCollection
    private List<String> relatedTopics;

    private LocalDateTime createdAt;

    public ResearchQuery() {
        this.createdAt = LocalDateTime.now();
    }

    // Add all getters and setters including the new ones
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getComprehensiveResponse() { return comprehensiveResponse; }
    public void setComprehensiveResponse(String comprehensiveResponse) { this.comprehensiveResponse = comprehensiveResponse; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getKeyPoints() { return keyPoints; }
    public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }
    public List<String> getSourceUrls() { return sourceUrls; }
    public void setSourceUrls(List<String> sourceUrls) { this.sourceUrls = sourceUrls; }
    public List<String> getDiagramIdeas() { return diagramIdeas; }
    public void setDiagramIdeas(List<String> diagramIdeas) { this.diagramIdeas = diagramIdeas; }
    public List<String> getRelatedTopics() { return relatedTopics; }
    public void setRelatedTopics(List<String> relatedTopics) { this.relatedTopics = relatedTopics; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}