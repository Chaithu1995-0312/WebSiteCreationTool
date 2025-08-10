package com.example.info.service;

import com.example.info.model.ResearchQuery;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AiResponseGeneratorService {

    private final ChatClient.Builder chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiResponseGeneratorService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient;
    }

    public ResearchQuery generateComprehensiveResponse(String topic, String synthesizedContent) {
        String prompt = String.format("""
        Based on the following research content about "%s", create a comprehensive response with:
        
        1. A clear title
        2. A detailed comprehensive explanation (300-500 words)
        3. A concise summary (50-100 words)
        4. 5-7 key facts and figures as bullet points
        5. 5-7 diagram/visualization ideas that would help explain this topic
        6. 4-6 related topics for further exploration
        
        Return your answer in JSON format with fields: 
        title, summary, comprehensiveResponse, keyPoints (array), diagramIdeas (array), relatedTopics (array).
        
        Research Content:
        %s
        """, topic, synthesizedContent);

        try {
            String aiResponse = chatClient.build().prompt(prompt).call().content();
            return parseAiResponse(aiResponse, topic);
        } catch (Exception e) {
            return createFallbackResponse(topic, synthesizedContent);
        }
    }

    private ResearchQuery parseAiResponse(String aiResponse, String topic) {
        ResearchQuery result = new ResearchQuery();
        result.setTopic(topic);

        try {
            // Clean JSON response (remove markdown formatting if present)
            String cleanJson = aiResponse.replaceAll("```json|```", "").trim();
            JsonNode node = objectMapper.readTree(cleanJson);

            result.setTitle(node.path("title").asText(topic));
            result.setComprehensiveResponse(node.path("comprehensiveResponse").asText(""));
            result.setSummary(node.path("summary").asText(""));

            // Parse keyPoints
            List<String> keyPoints = new ArrayList<>();
            if (node.has("keyPoints") && node.get("keyPoints").isArray()) {
                for (JsonNode point : node.get("keyPoints")) {
                    keyPoints.add(point.asText());
                }
            }
            result.setKeyPoints(keyPoints);

            // Parse diagramIdeas
            List<String> diagramIdeas = new ArrayList<>();
            if (node.has("diagramIdeas") && node.get("diagramIdeas").isArray()) {
                for (JsonNode idea : node.get("diagramIdeas")) {
                    diagramIdeas.add(idea.asText());
                }
            }
            result.setDiagramIdeas(diagramIdeas);

            // Parse relatedTopics
            List<String> relatedTopics = new ArrayList<>();
            if (node.has("relatedTopics") && node.get("relatedTopics").isArray()) {
                for (JsonNode topic_item : node.get("relatedTopics")) {
                    relatedTopics.add(topic_item.asText());
                }
            }
            result.setRelatedTopics(relatedTopics);

        } catch (Exception e) {
            return createFallbackResponse(topic, aiResponse);
        }

        return result;
    }

    private ResearchQuery createFallbackResponse(String topic, String content) {
        ResearchQuery result = new ResearchQuery();
        result.setTopic(topic);
        result.setTitle(topic);
        result.setComprehensiveResponse(content.length() > 500 ? content.substring(0, 500) + "..." : content);
        result.setSummary("Research results for: " + topic);
        result.setKeyPoints(Arrays.asList("Research conducted", "Content analyzed", "Information synthesized"));

        // Add fallback diagram ideas
        result.setDiagramIdeas(Arrays.asList(
                "Conceptual overview diagram",
                "Process flow chart",
                "Component relationship diagram",
                "Timeline visualization",
                "Comparison matrix"
        ));

        // Add fallback related topics
        result.setRelatedTopics(Arrays.asList(
                "Related concept 1",
                "Related concept 2",
                "Advanced applications",
                "Industry applications"
        ));

        return result;
    }
}