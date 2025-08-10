package com.example.info.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OpenAiService {

    private final ChatClient.Builder chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient;
    }

    public ContentGenerationResponse generateContent(String topic, String researchData) {
        String structuredPrompt = createStructuredPrompt(topic, researchData);

        try {
            String aiResponse = chatClient.build().prompt(structuredPrompt).call().content();
            System.out.println("AI Response: " + aiResponse); // Debug log
            return parseContentResponse(aiResponse, topic);
        } catch (Exception e) {
            System.err.println("AI Service failed: " + e.getMessage()); // Debug log
            return createFallbackResponse(topic, researchData);
        }
    }

    private String createStructuredPrompt(String topic, String researchData) {
        return String.format("""
        You are an expert content creator. Based on the research about "%s", create comprehensive content.
        
        Provide your response in EXACTLY this JSON format:
        {
          "title": "A compelling title for %s",
          "articleText": "A detailed 800-1200 word article with proper paragraphs and formatting",
          "summary": "A concise 2-3 sentence summary",
          "diagramIdeas": [
            "Architecture diagram showing %s components",
            "Workflow diagram illustrating %s process",
            "Comparison chart of %s vs traditional methods", 
            "Timeline showing %s evolution",
            "Mind map of %s key concepts",
            "Use case diagram for %s applications"
          ],
          "keyInsights": [
            "5-8 actionable insights about %s",
            "Key statistics and facts",
            "Important considerations",
            "Future trends and implications"
          ],
          "relatedTopics": [
            "4-6 related topics for further exploration",
            "Connected concepts and technologies",
            "Advanced applications",
            "Industry-specific uses"
          ]
        }
        
        Research Data: %s
        
        Remember: Return ONLY the JSON object, no additional text or markdown formatting.
        """, topic, topic, topic, topic, topic, topic, topic, topic, topic, researchData);
    }

    private ContentGenerationResponse parseContentResponse(String aiResponse, String topic) {
        ContentGenerationResponse response = new ContentGenerationResponse();

        try {
            // Clean the response - remove any markdown or extra text
            String cleanJson = aiResponse.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }
            cleanJson = cleanJson.trim();

            JsonNode node = objectMapper.readTree(cleanJson);

            response.setTitle(node.path("title").asText(topic));
            response.setArticleText(node.path("articleText").asText(""));
            response.setSummary(node.path("summary").asText(""));

            response.setDiagramIdeas(parseArrayField(node, "diagramIdeas"));
            response.setKeyInsights(parseArrayField(node, "keyInsights"));
            response.setRelatedTopics(parseArrayField(node, "relatedTopics"));

        } catch (Exception e) {
            System.err.println("Failed to parse AI response: " + e.getMessage());
            return createFallbackResponse(topic, aiResponse);
        }

        return response;
    }

    private List<String> parseArrayField(JsonNode node, String fieldName) {
        List<String> items = new ArrayList<>();
        if (node.has(fieldName) && node.get(fieldName).isArray()) {
            for (JsonNode item : node.get(fieldName)) {
                items.add(item.asText());
            }
        }
        return items;
    }

    // HERE'S THE MISSING METHOD:
    private ContentGenerationResponse createFallbackResponse(String topic, String content) {
        ContentGenerationResponse response = new ContentGenerationResponse();
        response.setTitle("Understanding " + topic);
        response.setArticleText(content.length() > 1000 ? content.substring(0, 1000) + "..." : content);
        response.setSummary("Comprehensive overview of " + topic + " covering key concepts, applications, and future implications.");

        // Enhanced diagram ideas specific to the topic
        response.setDiagramIdeas(generateTopicSpecificDiagrams(topic));
        response.setKeyInsights(generateTopicSpecificInsights(topic));
        response.setRelatedTopics(generateTopicSpecificRelated(topic));

        return response;
    }

    private List<String> generateTopicSpecificDiagrams(String topic) {
        String lowerTopic = topic.toLowerCase();
        List<String> diagrams = new ArrayList<>();

        if (lowerTopic.contains("ai") || lowerTopic.contains("artificial intelligence") || lowerTopic.contains("generative")) {
            diagrams.addAll(Arrays.asList(
                    "AI System Architecture showing data flow and processing layers",
                    "Machine Learning Pipeline from data ingestion to model deployment",
                    "AI vs Human Intelligence comparison matrix",
                    "Timeline of AI development milestones",
                    "AI Application Areas mind map (healthcare, finance, automotive, etc.)",
                    "Neural Network Structure diagram showing layers and connections",
                    "AI Ethics Framework flowchart for responsible development"
            ));
        } else if (lowerTopic.contains("cloud")) {
            diagrams.addAll(Arrays.asList(
                    "Cloud Architecture diagram with IaaS, PaaS, SaaS layers",
                    "Data migration workflow to cloud infrastructure",
                    "Multi-cloud deployment strategy visualization",
                    "Cloud security framework and protocols",
                    "Cost optimization decision tree for cloud services",
                    "Hybrid cloud infrastructure layout and connections"
            ));
        } else if (lowerTopic.contains("blockchain")) {
            diagrams.addAll(Arrays.asList(
                    "Blockchain network architecture and node distribution",
                    "Transaction flow diagram through blockchain",
                    "Consensus mechanism comparison chart",
                    "Smart contract execution workflow",
                    "Cryptocurrency ecosystem map",
                    "Blockchain vs traditional database comparison"
            ));
        } else {
            diagrams.addAll(Arrays.asList(
                    "System architecture overview for " + topic,
                    "Process workflow diagram for " + topic + " implementation",
                    "Component interaction diagram showing relationships",
                    "Timeline showing " + topic + " evolution and milestones",
                    "Comparison matrix of different " + topic + " approaches",
                    "Use case scenarios and applications for " + topic,
                    "Decision tree for implementing " + topic + " solutions"
            ));
        }

        return diagrams;
    }

    private List<String> generateTopicSpecificInsights(String topic) {
        String lowerTopic = topic.toLowerCase();
        List<String> insights = new ArrayList<>();

        if (lowerTopic.contains("ai") || lowerTopic.contains("artificial intelligence") || lowerTopic.contains("generative")) {
            insights.addAll(Arrays.asList(
                    "AI is transforming industries at an unprecedented pace",
                    "Generative AI models require massive computational resources and data",
                    "Ethical considerations are crucial for responsible AI development",
                    "AI augments human capabilities rather than replacing them entirely",
                    "Continuous learning and adaptation are key features of modern AI systems",
                    "Data quality and bias directly impact AI model performance",
                    "AI adoption requires significant organizational change management"
            ));
        } else {
            insights.addAll(Arrays.asList(
                    topic + " is rapidly transforming multiple industries worldwide",
                    "Implementation requires careful planning and strategic approach",
                    "Key benefits include significant efficiency gains and innovation opportunities",
                    "Main challenges involve technical complexity and effective change management",
                    "Future trends point toward increased adoption and deeper integration",
                    "Success depends on proper training and organizational readiness",
                    "Cost-benefit analysis is essential before " + topic + " implementation"
            ));
        }

        return insights;
    }

    private List<String> generateTopicSpecificRelated(String topic) {
        String lowerTopic = topic.toLowerCase();
        List<String> related = new ArrayList<>();

        if (lowerTopic.contains("ai") || lowerTopic.contains("artificial intelligence") || lowerTopic.contains("generative")) {
            related.addAll(Arrays.asList(
                    "Machine Learning Fundamentals and Algorithms",
                    "Deep Learning and Neural Networks",
                    "Natural Language Processing Applications",
                    "Computer Vision and Image Recognition",
                    "AI Ethics and Responsible Development",
                    "Future of AI Technology and Society"
            ));
        } else if (lowerTopic.contains("cloud")) {
            related.addAll(Arrays.asList(
                    "DevOps and Continuous Integration",
                    "Microservices Architecture",
                    "Containerization and Kubernetes",
                    "Cloud Security Best Practices",
                    "Serverless Computing",
                    "Edge Computing and IoT"
            ));
        } else {
            related.addAll(Arrays.asList(
                    "Advanced " + topic + " implementation techniques",
                    topic + " best practices and methodologies",
                    "Industry-specific applications of " + topic,
                    "Future trends and innovations in " + topic,
                    "Related technologies and complementary tools",
                    "Strategic implementation and adoption strategies"
            ));
        }

        return related;
    }

    // Keep the existing ContentGenerationResponse class
    public static class ContentGenerationResponse {
        private String title;
        private String articleText;
        private String summary;
        private List<String> diagramIdeas;
        private List<String> keyInsights;
        private List<String> relatedTopics;

        // Getters and setters
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
    }
}