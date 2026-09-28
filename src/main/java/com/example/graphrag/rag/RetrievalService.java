package com.example.graphrag.rag;

import com.example.graphrag.entity.DocumentChunk;
import com.example.graphrag.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalService {

    private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;

    public RetrievalService(DocumentRepository documentRepository, EmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunk> search(String question) {
        return documentRepository.similaritySearch(embeddingService.embed(question), 5);
    }
}