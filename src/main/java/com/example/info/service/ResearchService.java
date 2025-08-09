package com.example.info.service;

import com.example.info.model.ResearchResult;
import com.example.info.repository.ResearchResultRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class ResearchService {
    private final ResearchResultRepository repository;
    private final ChatClient.Builder chatClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${serpapi.api.key}")
    private String serpApiKey;

    private final String serpApiUrl = "https://serpapi.com/search.json?q=%s&api_key=%s";


    @Value("${bing.api.key}")
    private String bingApiKey;

    private final String bingSearchUrl = "https://api.bing.microsoft.com/v7.0/search?q=%s";


    public ResearchService(ChatClient.Builder chatClientBuilder, ResearchResultRepository repository) {
        this.chatClient = chatClientBuilder;
        this.repository = repository;
    }

    public ResearchResult researchTopic(String topic) throws Exception {
        ResearchResult cached = repository.findFirstByTopicIgnoreCase(topic);
        if (cached != null) return cached;

        // 1. Search the web for relevant URLs using SerpAPI
        List<String> urls = searchWebForUrls(topic);

        // 2. Scrape each URL
        List<String> scrapedTexts = new ArrayList<>();
        List<String> usedUrls = new ArrayList<>();
        String title = topic;

        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url).get();
                String text = doc.select("p").text();
                if (text.length() > 200) {
                    scrapedTexts.add(text);
                    usedUrls.add(url);
                    if (doc.title() != null && !doc.title().isEmpty()) title = doc.title();
                }
            } catch (Exception ignored) {}
        }

        if (scrapedTexts.isEmpty()) throw new RuntimeException("No information found for topic: " + topic);

        String combinedText = String.join("\n", scrapedTexts);

        String prompt = """
        Summarize the following text and extract key facts and figures.
        Return your answer in JSON format with fields: title, summary, keyFacts (array).
        Text:
        %s
        """.formatted(combinedText);

        String aiResponse = chatClient.build().prompt(prompt).call().content();
        ResearchResult result = parseAiResponse(aiResponse);
        result.setTopic(topic);
        result.setSourceUrls(usedUrls);

        repository.save(result);
        return result;
    }

    // SerpAPI Web Search client
    private List<String> searchWebForUrls(String topic) {
        String url = String.format(serpApiUrl, topic, serpApiKey);
        String response = restTemplate.getForObject(url, String.class);

        List<String> urls = new ArrayList<>();
        JSONObject json = new JSONObject(response);
        JSONArray organicResults = json.optJSONArray("organic_results");
        if (organicResults != null) {
            for (int i = 0; i < Math.min(organicResults.length(), 5); i++) {
                JSONObject result = organicResults.getJSONObject(i);
                if (result.has("link")) {
                    urls.add(result.getString("link"));
                }
            }
        }
        return urls;
    }

    // Robust JSON parser using Jackson
    private ResearchResult parseAiResponse(String aiResponse) {
        ResearchResult result = new ResearchResult();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(aiResponse);

            result.setTitle(node.path("title").asText("Unknown"));
            result.setSummary(node.path("summary").asText(""));
            List<String> keyFacts = new ArrayList<>();
            if (node.has("keyFacts") && node.get("keyFacts").isArray()) {
                for (JsonNode fact : node.get("keyFacts")) {
                    keyFacts.add(fact.asText());
                }
            }
            result.setKeyFacts(keyFacts);
        } catch (Exception e) {
            result.setTitle("Unknown");
            result.setSummary(aiResponse);
            result.setKeyFacts(List.of());
        }
        return result;
    }
}