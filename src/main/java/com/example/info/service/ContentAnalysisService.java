package com.example.info.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContentAnalysisService {

    public List<String> extractContentFromUrls(List<String> urls) {
        List<String> contents = new ArrayList<>();

        for (String url : urls) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(5000)
                        .get();

                String content = doc.select("p, h1, h2, h3").text();
                if (content.length() > 100) {
                    contents.add(content);
                }
            } catch (Exception e) {
                // Skip problematic URLs
                System.err.println("Failed to scrape: " + url + " - " + e.getMessage());
            }
        }
        return contents;
    }

    public String synthesizeContent(List<String> contents) {
        StringBuilder combined = new StringBuilder();
        for (String content : contents) {
            combined.append(content).append("\n\n");
        }
        return combined.toString().trim();
    }
}