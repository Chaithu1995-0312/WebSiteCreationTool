package com.example.info.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().connectTimeout(Duration.ofSeconds(30)).readTimeout(Duration.ofSeconds(30))
                .build();
    }

    // Alternative: More customized RestTemplate

}