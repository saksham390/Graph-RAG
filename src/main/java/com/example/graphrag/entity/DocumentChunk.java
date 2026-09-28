package com.example.graphrag.entity;

public record DocumentChunk(Long id, Long documentId, int chunkIndex, String content, double score) {
}