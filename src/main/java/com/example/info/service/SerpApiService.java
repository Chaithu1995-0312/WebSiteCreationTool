package com.example.info.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SerpApiService {

    @Value("${serpapi.api.key:demo_key}")
    private String serpApiKey;
@Autowired
    RestTemplate restTemplate;
    private final String serpApiUrl = "https://serpapi.com/search.json?q=%s&api_key=%s&num=10&hl=en";
    // In SerpApiService.java - add this method
    public List<SearchResult> searchWebResults(String query) {
        SerpApiResponse response = searchWeb(query);
        return response.getResults();
    }
    public SerpApiResponse searchWeb(String query) {
        try {
            String url = String.format(serpApiUrl, query, serpApiKey);
            String response = restTemplate.getForObject(url, String.class);
            return parseSearchResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("SERP API search failed: " + e.getMessage());
        }
    }

    private SerpApiResponse parseSearchResponse(String response) {
        SerpApiResponse apiResponse = new SerpApiResponse();
        JSONObject json = new JSONObject(response);

        // Parse organic results
        JSONArray organicResults = json.optJSONArray("organic_results");
        List<SearchResult> results = new ArrayList<>();

        if (organicResults != null) {
            for (int i = 0; i < organicResults.length(); i++) {
                JSONObject result = organicResults.getJSONObject(i);
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(result.optString("title", ""));
                searchResult.setLink(result.optString("link", ""));
                searchResult.setSnippet(result.optString("snippet", ""));
                searchResult.setPosition(result.optInt("position", i + 1));
                results.add(searchResult);
            }
        }

        apiResponse.setResults(results);
        apiResponse.setSearchMetadata(parseSearchMetadata(json));
        return apiResponse;
    }

    private SearchMetadata parseSearchMetadata(JSONObject json) {
        SearchMetadata metadata = new SearchMetadata();
        JSONObject searchMetadata = json.optJSONObject("search_metadata");
        if (searchMetadata != null) {
            metadata.setQueryDisplayed(searchMetadata.optString("query_displayed", ""));
            metadata.setTotalResults(searchMetadata.optString("total_results", "0"));
            metadata.setTimeTaken(searchMetadata.optDouble("time_taken_displayed", 0.0));
        }
        return metadata;
    }

    // Inner classes for structured responses
    public static class SerpApiResponse {
        private List<SearchResult> results;
        private SearchMetadata searchMetadata;

        // Getters and setters
        public List<SearchResult> getResults() { return results; }
        public void setResults(List<SearchResult> results) { this.results = results; }
        public SearchMetadata getSearchMetadata() { return searchMetadata; }
        public void setSearchMetadata(SearchMetadata searchMetadata) { this.searchMetadata = searchMetadata; }
    }

    public static class SearchResult {
        private String title;
        private String link;
        private String snippet;
        private int position;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        public String getSnippet() { return snippet; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
    }

    public static class SearchMetadata {
        private String queryDisplayed;
        private String totalResults;
        private double timeTaken;

        // Getters and setters
        public String getQueryDisplayed() { return queryDisplayed; }
        public void setQueryDisplayed(String queryDisplayed) { this.queryDisplayed = queryDisplayed; }
        public String getTotalResults() { return totalResults; }
        public void setTotalResults(String totalResults) { this.totalResults = totalResults; }
        public double getTimeTaken() { return timeTaken; }
        public void setTimeTaken(double timeTaken) { this.timeTaken = timeTaken; }
    }
}