package com.example.graphrag.dto;

import jakarta.validation.constraints.NotBlank;

public record QueryRequest(@NotBlank String question) {
}