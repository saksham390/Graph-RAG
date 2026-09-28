package com.example.graphrag.dto;

public record DocumentUploadResponse(Long documentId, String fileName, int chunkCount, int relationshipCount) {
}