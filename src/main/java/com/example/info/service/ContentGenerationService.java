package com.example.info.service;

import com.example.info.model.GeneratedContent;
import com.example.info.repository.GeneratedContentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentGenerationService {

    private final SerpApiService serpApiService;
    private final ContentAnalysisService contentAnalysisService;
    private final OpenAiService openAiService;
    private final GeneratedContentRepository contentRepository;

    public ContentGenerationService(
            SerpApiService serpApiService,
            ContentAnalysisService contentAnalysisService,
            OpenAiService openAiService,
            GeneratedContentRepository contentRepository) {
        this.serpApiService = serpApiService;
        this.contentAnalysisService = contentAnalysisService;
        this.openAiService = openAiService;
        this.contentRepository = contentRepository;
    }

    public GeneratedContent generateContent(String topic) {
        try {
            // Step 1: Search the web using SERP API
            SerpApiService.SerpApiResponse searchResponse = serpApiService.searchWeb(topic);

            // Step 2: Extract URLs and scrape content
            List<String> urls = searchResponse.getResults().stream()
                    .map(SerpApiService.SearchResult::getLink)
                    .collect(Collectors.toList());

            List<String> scrapedContent = contentAnalysisService.extractContentFromUrls(urls);
            String synthesizedData = contentAnalysisService.synthesizeContent(scrapedContent);

            // Step 3: Generate structured content using OpenAI
            OpenAiService.ContentGenerationResponse aiResponse =
                    openAiService.generateContent(topic, synthesizedData);

            // Step 4: Create and save the generated content
            GeneratedContent content = new GeneratedContent();
            content.setTopic(topic);
            content.setTitle(aiResponse.getTitle());
            content.setArticleText(aiResponse.getArticleText());
            content.setSummary(aiResponse.getSummary());
            content.setDiagramIdeas(aiResponse.getDiagramIdeas());
            content.setKeyInsights(aiResponse.getKeyInsights());
            content.setRelatedTopics(aiResponse.getRelatedTopics());
            content.setSourceUrls(urls);
            content.setStatus("GENERATED");

            return contentRepository.save(content);

        } catch (Exception e) {
            throw new RuntimeException("Content generation failed for topic: " + topic + ". Error: " + e.getMessage());
        }
    }

    public GeneratedContent regenerateContent(Long contentId, String topic) {
        // Delete existing content and generate new
        contentRepository.deleteById(contentId);
        return generateContent(topic);
    }
}
