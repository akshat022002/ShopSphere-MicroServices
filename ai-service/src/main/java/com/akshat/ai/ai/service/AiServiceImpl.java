package com.akshat.ai.ai.service;

import com.akshat.ai.ai.tool.ProductAiTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService{

    private final ChatClient chatClient;
    private final ProductAiTool productAiTool;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder, ProductAiTool productAiTool){
        this.chatClient=chatClientBuilder.build();
        this.productAiTool=productAiTool;
    }

    @Override
    public String ask(String question) {
        return chatClient
                .prompt("""
                   You are ShopSphere AI Assistant.
                   
                   You can answer general shopping questions.
                   
                   When the user asks about products available in ShopSphere,
                   use the ShopSphere product search tool.
                   
                   Never invent ShopSphere products or prices.
                   """
                )
                .user(question)
                .tools(productAiTool)
                .call()
                .content();
    }
}
