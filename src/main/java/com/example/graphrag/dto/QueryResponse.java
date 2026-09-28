package com.example.graphrag.dto;

import java.util.List;

public record QueryResponse(String question, String answer, List<String> documentContext,
                            List<String> graphContext) {
}