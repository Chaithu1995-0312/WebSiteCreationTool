package com.example.info.service;

import com.example.info.model.ResearchQuery;
import com.example.info.repository.ResearchQueryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchService {

    private final SerpApiService serpApiService;
    private final ContentAnalysisService contentAnalysisService;
    private final AiResponseGeneratorService aiResponseGeneratorService;
    private final ResearchQueryRepository repository;

    public ResearchService(SerpApiService serpApiService,
                           ContentAnalysisService contentAnalysisService,
                           AiResponseGeneratorService aiResponseGeneratorService,
                           ResearchQueryRepository repository) {
        this.serpApiService = serpApiService;
        this.contentAnalysisService = contentAnalysisService;
        this.aiResponseGeneratorService = aiResponseGeneratorService;
        this.repository = repository;
    }

    public ResearchQuery conductResearch(String topic) {
        // Step 1: Check cache
        ResearchQuery cached = repository.findFirstByTopicIgnoreCase(topic);
        if (cached != null) {
            return cached;
        }

        try {
            // Step 2: Send query to SERP API - FIX: Get the response object first
            SerpApiService.SerpApiResponse searchResponse = serpApiService.searchWeb(topic);

            // Step 3: Extract search results from the response
            List<SerpApiService.SearchResult> searchResults = searchResponse.getResults();

            // Step 4: Extract URLs
            List<String> urls = searchResults.stream()
                    .map(SerpApiService.SearchResult::getLink)
                    .collect(Collectors.toList());

            // Step 5: Analyze and synthesize content
            List<String> contents = contentAnalysisService.extractContentFromUrls(urls);
            String synthesizedContent = contentAnalysisService.synthesizeContent(contents);

            // Step 6: Generate comprehensive response using AI
            ResearchQuery result = aiResponseGeneratorService.generateComprehensiveResponse(topic, synthesizedContent);

            // Step 7: Store source URLs
            result.setSourceUrls(urls);

            // Step 8: Save to database
            repository.save(result);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Research failed for topic: " + topic + ". Error: " + e.getMessage());
        }
    }
}