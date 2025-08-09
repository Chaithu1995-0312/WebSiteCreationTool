package com.example.info.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class ResearchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String title;
    @Column(length = 4000)
    private String summary;

    @ElementCollection
    private List<String> keyFacts;

    @ElementCollection
    private List<String> sourceUrls;

    // Getters and setters
    public Long getId() { return id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getKeyFacts() { return keyFacts; }
    public void setKeyFacts(List<String> keyFacts) { this.keyFacts = keyFacts; }
    public List<String> getSourceUrls() { return sourceUrls; }
    public void setSourceUrls(List<String> sourceUrls) { this.sourceUrls = sourceUrls; }
}