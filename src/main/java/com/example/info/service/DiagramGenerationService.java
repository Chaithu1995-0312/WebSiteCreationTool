package com.example.info.service;

import com.example.info.model.DiagramRequest;
import com.example.info.model.DiagramResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class DiagramGenerationService {

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${lucidchart.api.key:}")
    private String lucidchartApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MermaidService mermaidService;

    public DiagramGenerationService(RestTemplate restTemplate,
                                    ObjectMapper objectMapper,
                                    MermaidService mermaidService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.mermaidService = mermaidService;
    }

    public DiagramResponse generateDiagram(DiagramRequest request) {
        DiagramType type = determineDiagramType(request.getDescription());

        return switch (type) {
            case ARCHITECTURE -> generateMermaidDiagram(request);
            case FLOWCHART, WORKFLOW -> generateMermaidFlowchart(request);
            case TIMELINE -> generateD3Timeline(request);
            case CONCEPTUAL, ARTISTIC -> generateDalleImage(request);
            case PROFESSIONAL -> generateLucidchartDiagram(request);
            default -> generateFallbackSVG(request);
        };
    }

    private DiagramType determineDiagramType(String description) {
        String lower = description.toLowerCase();

        if (lower.contains("architecture") || lower.contains("system") || lower.contains("component")) {
            return DiagramType.ARCHITECTURE;
        } else if (lower.contains("workflow") || lower.contains("process") || lower.contains("flow")) {
            return DiagramType.FLOWCHART;
        } else if (lower.contains("timeline") || lower.contains("evolution") || lower.contains("history")) {
            return DiagramType.TIMELINE;
        } else if (lower.contains("conceptual") || lower.contains("abstract") || lower.contains("artistic")) {
            return DiagramType.CONCEPTUAL;
        } else if (lower.contains("professional") || lower.contains("business") || lower.contains("presentation")) {
            return DiagramType.PROFESSIONAL;
        }
        return DiagramType.MERMAID;
    }

    // DALL-E Integration for AI-Generated Diagrams
    private DiagramResponse generateDalleImage(DiagramRequest request) {
        try {
            String enhancedPrompt = createDallePrompt(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> requestBody = Map.of(
                    "model", "dall-e-3",
                    "prompt", enhancedPrompt,
                    "n", 1,
                    "size", "1024x1024",
                    "quality", "standard",
                    "style", "vivid"
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/images/generations",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String imageUrl = jsonResponse.path("data").get(0).path("url").asText();

                return DiagramResponse.builder()
                        .type(DiagramType.CONCEPTUAL)
                        .format("PNG")
                        .imageUrl(imageUrl)
                        .description(request.getDescription())
                        .generatedAt(new Date())
                        .provider("DALL-E-3")
                        .build();
            }
        } catch (Exception e) {
            System.err.println("DALL-E generation failed: " + e.getMessage());
        }

        return generateFallbackSVG(request);
    }

    private String createDallePrompt(DiagramRequest request) {
        return String.format("""
        Create a professional, clean diagram illustration for: %s
        
        Style requirements:
        - Clean, modern design
        - Technical/business appropriate
        - Clear visual hierarchy
        - Minimal color palette (blues, grays, whites)
        - No text overlays (diagram should be self-explanatory)
        - High contrast for readability
        - Professional presentation quality
        
        The diagram should visually represent the concept in a way that would be suitable 
        for a business presentation or technical documentation.
        """, request.getDescription());
    }

    // Mermaid.js Integration for Code-Based Diagrams
    private DiagramResponse generateMermaidDiagram(DiagramRequest request) {
        String mermaidCode = mermaidService.generateMermaidCode(request);
        String svgContent = mermaidService.renderToSvg(mermaidCode);

        return DiagramResponse.builder()
                .type(DiagramType.ARCHITECTURE)
                .format("SVG")
                .svgContent(svgContent)
                .mermaidCode(mermaidCode)
                .description(request.getDescription())
                .generatedAt(new Date())
                .provider("Mermaid.js")
                .build();
    }

    private DiagramResponse generateMermaidFlowchart(DiagramRequest request) {
        String mermaidCode = mermaidService.generateFlowchartCode(request);
        String svgContent = mermaidService.renderToSvg(mermaidCode);

        return DiagramResponse.builder()
                .type(DiagramType.FLOWCHART)
                .format("SVG")
                .svgContent(svgContent)
                .mermaidCode(mermaidCode)
                .description(request.getDescription())
                .generatedAt(new Date())
                .provider("Mermaid.js")
                .build();
    }

    // D3.js Integration for Data Visualizations
    private DiagramResponse generateD3Timeline(DiagramRequest request) {
        try {
            // Call to a microservice or internal service that handles D3 rendering
            String d3Config = createD3TimelineConfig(request);
            String svgContent = renderD3ToSvg(d3Config);

            return DiagramResponse.builder()
                    .type(DiagramType.TIMELINE)
                    .format("SVG")
                    .svgContent(svgContent)
                    .d3Config(d3Config)
                    .description(request.getDescription())
                    .generatedAt(new Date())
                    .provider("D3.js")
                    .build();
        } catch (Exception e) {
            return generateFallbackSVG(request);
        }
    }

    // Lucidchart Integration for Professional Diagrams
    private DiagramResponse generateLucidchartDiagram(DiagramRequest request) {
        if (lucidchartApiKey == null || lucidchartApiKey.isEmpty()) {
            return generateMermaidDiagram(request); // Fallback to Mermaid
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + lucidchartApiKey);

            Map<String, Object> requestBody = Map.of(
                    "title", "AI Generated Diagram",
                    "description", request.getDescription(),
                    "template", "flowchart",
                    "autoLayout", true
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.lucidchart.com/documents",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String documentId = jsonResponse.path("id").asText();
                String exportUrl = "https://api.lucidchart.com/documents/" + documentId + "/export/svg";

                return DiagramResponse.builder()
                        .type(DiagramType.PROFESSIONAL)
                        .format("SVG")
                        .imageUrl(exportUrl)
                        .description(request.getDescription())
                        .generatedAt(new Date())
                        .provider("Lucidchart")
                        .documentId(documentId)
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Lucidchart generation failed: " + e.getMessage());
        }

        return generateMermaidDiagram(request); // Fallback
    }

    private DiagramResponse generateFallbackSVG(DiagramRequest request) {
        String svgContent = createBasicSVG(request.getDescription());

        return DiagramResponse.builder()
                .type(DiagramType.BASIC)
                .format("SVG")
                .svgContent(svgContent)
                .description(request.getDescription())
                .generatedAt(new Date())
                .provider("Internal")
                .build();
    }

    private String createD3TimelineConfig(DiagramRequest request) {
        // Generate D3.js configuration for timeline
        return """
        {
          "type": "timeline",
          "data": [
            {"date": "2020", "event": "AI Research Begins", "category": "research"},
            {"date": "2021", "event": "Model Development", "category": "development"},
            {"date": "2022", "event": "Testing Phase", "category": "testing"},
            {"date": "2023", "event": "Production Release", "category": "release"}
          ],
          "config": {
            "width": 800,
            "height": 300,
            "margin": {"top": 20, "right": 20, "bottom": 30, "left": 40}
          }
        }
        """;
    }

    private String renderD3ToSvg(String d3Config) {
        // In production, this would call a Node.js service that renders D3 to SVG
        // For now, return a placeholder SVG
        return """
        <svg width="800" height="300" xmlns="http://www.w3.org/2000/svg">
          <rect width="800" height="300" fill="#f8f9fa" stroke="#dee2e6"/>
          <text x="400" y="150" text-anchor="middle" font-size="16" fill="#333">
            D3.js Timeline Visualization
          </text>
          <text x="400" y="180" text-anchor="middle" font-size="12" fill="#666">
            Advanced timeline rendering would appear here
          </text>
        </svg>
        """;
    }

    private String createBasicSVG(String description) {
        return String.format("""
        <svg width="400" height="300" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="grad1" x1="0%%" y1="0%%" x2="100%%" y2="100%%">
              <stop offset="0%%" stop-color="#667eea"/>
              <stop offset="100%%" stop-color="#764ba2"/>
            </linearGradient>
          </defs>
          <rect width="400" height="300" fill="url(#grad1)" rx="10"/>
          <rect x="50" y="50" width="300" height="200" fill="white" rx="8" opacity="0.9"/>
          <text x="200" y="120" text-anchor="middle" font-size="14" fill="#333" font-weight="bold">
            %s
          </text>
          <text x="200" y="180" text-anchor="middle" font-size="12" fill="#666">
            Diagram visualization
          </text>
        </svg>
        """, description.length() > 50 ? description.substring(0, 50) + "..." : description);
    }

    // FIXED ENUM - Added missing values and removed SYSTEM
    public enum DiagramType {
        ARCHITECTURE,
        FLOWCHART,
        WORKFLOW,
        TIMELINE,
        CONCEPTUAL,
        ARTISTIC,
        PROFESSIONAL,
        MERMAID,
        BASIC
    }
}