package com.example.graphrag.ai;

import com.example.graphrag.dto.RelationshipDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GraphExtractionService {

    private static final Pattern RELATIONSHIP = Pattern.compile(
            "(?i)([A-Z][A-Za-z0-9]*(?:\\s+[A-Z][A-Za-z0-9]*)*)\\s+" +
                    "(acquired|developed|created|uses|works at)\\s+" +
                    "([A-Z][A-Za-z0-9]*(?:\\s+[A-Z][A-Za-z0-9]*)*)");

    private final ObjectProvider<ChatLanguageModel> chatModel;
    private final ObjectMapper objectMapper;

    public GraphExtractionService(ObjectProvider<ChatLanguageModel> chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public List<RelationshipDto> extract(String text) {
        ChatLanguageModel model = chatModel.getIfAvailable();
        if (model != null) {
            String prompt = "Extract factual relationships from this text. Return only a JSON array " +
                    "of objects with source, relationship, and target fields. Allowed relationships: " +
                    "ACQUIRED, DEVELOPED, CREATED, USES, WORKS_AT. Text:\n" + text;
            try {
                return objectMapper.readValue(model.generate(prompt), new TypeReference<>() {
                });
            } catch (Exception ignored) {
                // The deterministic parser below keeps ingestion usable when an LLM returns invalid JSON.
            }
        }
        return extractWithSimplePatterns(text);
    }

    private List<RelationshipDto> extractWithSimplePatterns(String text) {
        List<RelationshipDto> relationships = new ArrayList<>();
        Matcher matcher = RELATIONSHIP.matcher(text);
        while (matcher.find()) {
            relationships.add(new RelationshipDto(matcher.group(1).trim(),
                    matcher.group(2).toUpperCase().replace(' ', '_'), matcher.group(3).trim()));
        }
        return relationships;
    }
}