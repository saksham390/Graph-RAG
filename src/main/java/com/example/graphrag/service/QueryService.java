package com.example.graphrag.service;

import com.example.graphrag.ai.AnswerService;
import com.example.graphrag.dto.QueryResponse;
import com.example.graphrag.entity.DocumentChunk;
import com.example.graphrag.graph.GraphService;
import com.example.graphrag.rag.RetrievalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService {

    private final RetrievalService retrievalService;
    private final GraphService graphService;
    private final AnswerService answerService;

    public QueryService(RetrievalService retrievalService, GraphService graphService, AnswerService answerService) {
        this.retrievalService = retrievalService;
        this.graphService = graphService;
        this.answerService = answerService;
    }

    public QueryResponse vectorQuery(String question) {
        List<String> documents = retrievalService.search(question).stream()
                .map(DocumentChunk::content).toList();
        String answer = answerService.answer(question, documents, List.of());
        return new QueryResponse(question, answer, documents, List.of());
    }

    public QueryResponse graphRagQuery(String question) {
        List<String> documents = retrievalService.search(question).stream()
                .map(DocumentChunk::content).toList();
        List<String> graph = graphService.search(question);
        String answer = answerService.answer(question, documents, graph);
        return new QueryResponse(question, answer, documents, graph);
    }
}