package com.example.info.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;

@RestController
public class AiHelloController {

    private final ChatClient.Builder chatClientBuilder;

    public AiHelloController(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @GetMapping("/ai-hello")
    public String aiHello() {
        var chat = chatClientBuilder.build();
        String userPrompt = "Tell me a joke";
        String assistantReply = chat.prompt(userPrompt).call().content();
        return "Spring AI Hello World!\nUSER: " + userPrompt + "\nASSISTANT: " + assistantReply;
    }
}