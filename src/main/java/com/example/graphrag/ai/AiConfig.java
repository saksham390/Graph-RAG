package com.example.graphrag.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnProperty(name = "openai.api-key")
    public ChatLanguageModel chatLanguageModel(@Value("${openai.api-key}") String apiKey,
                                               @Value("${openai.chat-model:gpt-4o-mini}") String modelName) {
        return OpenAiChatModel.builder().apiKey(apiKey).modelName(modelName).build();
    }
}