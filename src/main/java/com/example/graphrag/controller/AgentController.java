package com.example.graphrag.controller;

import com.example.graphrag.ai.ToolCallingService;
import com.example.graphrag.dto.QueryRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ToolCallingService toolCallingService;

    public AgentController(ToolCallingService toolCallingService) {
        this.toolCallingService = toolCallingService;
    }

    @PostMapping("/query")
    public String query(@Valid @RequestBody QueryRequest request) {
        return toolCallingService.answer(request.question());
    }
}