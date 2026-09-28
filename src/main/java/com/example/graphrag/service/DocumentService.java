package com.example.graphrag.service;

import com.example.graphrag.ai.GraphExtractionService;
import com.example.graphrag.dto.DocumentUploadResponse;
import com.example.graphrag.dto.RelationshipDto;
import com.example.graphrag.graph.GraphService;
import com.example.graphrag.rag.EmbeddingService;
import com.example.graphrag.rag.TextChunker;
import com.example.graphrag.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final GraphExtractionService graphExtractionService;
    private final GraphService graphService;

    public DocumentService(DocumentRepository documentRepository, TextChunker textChunker,
                           EmbeddingService embeddingService, GraphExtractionService graphExtractionService,
                           GraphService graphService) {
        this.documentRepository = documentRepository;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.graphExtractionService = graphExtractionService;
        this.graphService = graphService;
    }

    public DocumentUploadResponse upload(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Only non-empty .txt files are supported");
        }

        String content = new String(file.getBytes());
        long documentId = documentRepository.saveDocument(file.getOriginalFilename(), content);
        List<String> chunks = textChunker.split(content);
        for (int index = 0; index < chunks.size(); index++) {
            documentRepository.saveChunk(documentId, index, chunks.get(index), embeddingService.embed(chunks.get(index)));
        }

        List<RelationshipDto> relationships = graphExtractionService.extract(content);
        graphService.saveRelationships(relationships);
        return new DocumentUploadResponse(documentId, file.getOriginalFilename(), chunks.size(), relationships.size());
    }
}