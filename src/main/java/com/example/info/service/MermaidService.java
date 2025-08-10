package com.example.info.service;

import com.example.info.model.DiagramRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.Base64;

@Service
public class MermaidService {

    private final RestTemplate restTemplate;

    public MermaidService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateMermaidCode(DiagramRequest request) {
        String description = request.getDescription().toLowerCase();

        if (description.contains("architecture") || description.contains("system")) {
            return generateArchitectureDiagram(request);
        } else if (description.contains("class") || description.contains("object")) {
            return generateClassDiagram(request);
        } else if (description.contains("sequence") || description.contains("interaction")) {
            return generateSequenceDiagram(request);
        } else {
            return generateFlowchartCode(request);
        }
    }

    public String generateFlowchartCode(DiagramRequest request) {
        return """
        flowchart TD
            A[Start: %s] --> B{Analysis}
            B -->|Yes| C[Process Data]
            B -->|No| D[Collect More Info]
            C --> E[Generate Results]
            D --> B
            E --> F[End: Complete]
            
            style A fill:#e1f5fe
            style F fill:#e8f5e8
            style B fill:#fff3e0
            style C fill:#f3e5f5
            style D fill:#fce4ec
            style E fill:#e0f2f1
        """.formatted(request.getDescription());
    }

    private String generateArchitectureDiagram(DiagramRequest request) {
        return """
        graph TB
            subgraph "Frontend Layer"
                UI[User Interface]
                API[API Gateway]
            end
            
            subgraph "Application Layer"
                SERVICE[%s Service]
                PROCESSOR[Data Processor]
            end
            
            subgraph "Data Layer"
                DB[(Database)]
                CACHE[(Cache)]
            end
            
            UI --> API
            API --> SERVICE
            SERVICE --> PROCESSOR
            PROCESSOR --> DB
            SERVICE --> CACHE
            
            style UI fill:#e3f2fd
            style SERVICE fill:#f3e5f5
            style DB fill:#e8f5e8
        """.formatted(request.getDescription());
    }

    private String generateClassDiagram(DiagramRequest request) {
        return """
        classDiagram
            class %sSystem {
                +String id
                +String name
                +Date createdAt
                +process()
                +validate()
                +save()
            }
            
            class DataProcessor {
                +analyzeData()
                +transformData()
                +exportResults()
            }
            
            class ConfigManager {
                +loadConfig()
                +updateSettings()
            }
            
            %sSystem --> DataProcessor
            %sSystem --> ConfigManager
        """.formatted(
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", "")
        );
    }

    private String generateSequenceDiagram(DiagramRequest request) {
        return """
        sequenceDiagram
            participant User
            participant Frontend
            participant API
            participant %sService
            participant Database
            
            User->>Frontend: Request %s
            Frontend->>API: HTTP Request
            API->>%sService: Process Request
            %sService->>Database: Query Data
            Database-->>%sService: Return Results
            %sService-->>API: Processed Data
            API-->>Frontend: JSON Response
            Frontend-->>User: Display Results
        """.formatted(
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription(),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", ""),
                request.getDescription().replaceAll("[^a-zA-Z0-9]", "")
        );
    }

    public String renderToSvg(String mermaidCode) {
        try {
            // Use Mermaid.ink API for rendering
            String encodedDiagram = Base64.getEncoder().encodeToString(mermaidCode.getBytes());
            String url = "https://mermaid.ink/svg/" + encodedDiagram;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Mermaid rendering failed: " + e.getMessage());
        }

        // Fallback SVG
        return """
        <svg width="400" height="300" xmlns="http://www.w3.org/2000/svg">
          <rect width="400" height="300" fill="#f8f9fa" stroke="#dee2e6"/>
          <text x="200" y="150" text-anchor="middle" font-size="16" fill="#333">
            Mermaid Diagram
          </text>
          <text x="200" y="180" text-anchor="middle" font-size="12" fill="#666">
            Code: %s
          </text>
        </svg>
        """.formatted(mermaidCode.substring(0, Math.min(50, mermaidCode.length())));
    }
}