package com.example.graphrag.controller;

import com.example.graphrag.dto.QueryRequest;
import com.example.graphrag.dto.QueryResponse;
import com.example.graphrag.service.QueryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/rag/query")
    public QueryResponse ragQuery(@Valid @RequestBody QueryRequest request) {
        return queryService.vectorQuery(request.question());
    }

    @PostMapping("/graphrag/query")
    public QueryResponse graphRagQuery(@Valid @RequestBody QueryRequest request) {
        return queryService.graphRagQuery(request.question());
    }
}