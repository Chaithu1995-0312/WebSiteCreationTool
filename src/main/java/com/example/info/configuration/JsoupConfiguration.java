package com.example.info.configuration;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupConfiguration {
    public static void main(String[] args) throws Exception {
        // Connect to the website
        Document doc = Jsoup.connect("https://example.com").get();

        // Extract the text inside the <h1> tag
        Element h1 = doc.selectFirst("h1");
        if (h1 != null) {
            System.out.println("Extracted text: " + h1.text());
        } else {
            System.out.println("<h1> tag not found.");
        }
    }
}