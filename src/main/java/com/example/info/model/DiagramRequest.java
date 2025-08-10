package com.example.info.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagramRequest {
    private String description;
    private String topic;
    private String preferredType; // "mermaid", "dalle", "d3", "lucidchart"
    private String style; // "professional", "technical", "artistic"
    private String format; // "svg", "png", "pdf"
    private Integer width = 800;
    private Integer height = 600;
    private String colorScheme = "blue"; // "blue", "green", "purple", "monochrome"

    // Default constructor
    public DiagramRequest() {}

    // Constructor with description
    public DiagramRequest(String description) {
        this.description = description;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(String preferredType) {
        this.preferredType = preferredType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    @Override
    public String toString() {
        return "DiagramRequest{" +
                "description='" + description + '\'' +
                ", topic='" + topic + '\'' +
                ", preferredType='" + preferredType + '\'' +
                ", style='" + style + '\'' +
                ", format='" + format + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", colorScheme='" + colorScheme + '\'' +
                '}';
    }
}