package com.example.info.model;

import com.example.info.service.DiagramGenerationService.DiagramType;
import java.util.Date;

public class DiagramResponse {
    private DiagramType type;
    private String format;
    private String svgContent;
    private String imageUrl;
    private String mermaidCode;
    private String d3Config;
    private String description;
    private Date generatedAt;
    private String provider;
    private String documentId;
    private String downloadUrl;
    private Integer width;
    private Integer height;

    // Default constructor
    public DiagramResponse() {}

    // Builder pattern
    public static DiagramResponseBuilder builder() {
        return new DiagramResponseBuilder();
    }

    // Getters and Setters
    public DiagramType getType() {
        return type;
    }

    public void setType(DiagramType type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSvgContent() {
        return svgContent;
    }

    public void setSvgContent(String svgContent) {
        this.svgContent = svgContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMermaidCode() {
        return mermaidCode;
    }

    public void setMermaidCode(String mermaidCode) {
        this.mermaidCode = mermaidCode;
    }

    public String getD3Config() {
        return d3Config;
    }

    public void setD3Config(String d3Config) {
        this.d3Config = d3Config;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    // Builder class
    public static class DiagramResponseBuilder {
        private final DiagramResponse diagramResponse = new DiagramResponse();

        public DiagramResponseBuilder type(DiagramType type) {
            diagramResponse.setType(type);
            return this;
        }

        public DiagramResponseBuilder format(String format) {
            diagramResponse.setFormat(format);
            return this;
        }

        public DiagramResponseBuilder svgContent(String svgContent) {
            diagramResponse.setSvgContent(svgContent);
            return this;
        }

        public DiagramResponseBuilder imageUrl(String imageUrl) {
            diagramResponse.setImageUrl(imageUrl);
            return this;
        }

        public DiagramResponseBuilder mermaidCode(String mermaidCode) {
            diagramResponse.setMermaidCode(mermaidCode);
            return this;
        }

        public DiagramResponseBuilder d3Config(String d3Config) {
            diagramResponse.setD3Config(d3Config);
            return this;
        }

        public DiagramResponseBuilder description(String description) {
            diagramResponse.setDescription(description);
            return this;
        }

        public DiagramResponseBuilder generatedAt(Date generatedAt) {
            diagramResponse.setGeneratedAt(generatedAt);
            return this;
        }

        public DiagramResponseBuilder provider(String provider) {
            diagramResponse.setProvider(provider);
            return this;
        }

        public DiagramResponseBuilder documentId(String documentId) {
            diagramResponse.setDocumentId(documentId);
            return this;
        }

        public DiagramResponseBuilder downloadUrl(String downloadUrl) {
            diagramResponse.setDownloadUrl(downloadUrl);
            return this;
        }

        public DiagramResponseBuilder width(Integer width) {
            diagramResponse.setWidth(width);
            return this;
        }

        public DiagramResponseBuilder height(Integer height) {
            diagramResponse.setHeight(height);
            return this;
        }

        public DiagramResponse build() {
            return diagramResponse;
        }
    }

    @Override
    public String toString() {
        return "DiagramResponse{" +
                "type=" + type +
                ", format='" + format + '\'' +
                ", description='" + description + '\'' +
                ", provider='" + provider + '\'' +
                ", generatedAt=" + generatedAt +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}