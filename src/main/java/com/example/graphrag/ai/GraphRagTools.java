package com.example.graphrag.ai;

import com.example.graphrag.graph.GraphService;
import com.example.graphrag.rag.RetrievalService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphRagTools {

    private final RetrievalService retrievalService;
    private final GraphService graphService;

    public GraphRagTools(RetrievalService retrievalService, GraphService graphService) {
        this.retrievalService = retrievalService;
        this.graphService = graphService;
    }

    @Tool("Search document chunks for semantic evidence")
    public List<String> searchDocuments(String question) {
        return retrievalService.search(question).stream().map(chunk -> chunk.content()).toList();
    }

    @Tool("Search the knowledge graph for entities and relationships")
    public List<String> searchGraph(String question) {
        return graphService.search(question);
    }
}