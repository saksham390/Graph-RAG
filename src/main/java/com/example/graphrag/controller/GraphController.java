package com.example.graphrag.controller;

import com.example.graphrag.graph.GraphService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/entities")
    public List<String> entities() {
        return graphService.entities();
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String question) {
        return graphService.search(question);
    }
}