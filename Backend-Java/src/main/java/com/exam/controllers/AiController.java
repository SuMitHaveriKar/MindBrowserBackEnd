package com.exam.controllers;

import com.exam.services.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateContent(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Prompt is required"));
        }
        String generatedContent = aiService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("content", generatedContent));
    }

    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarizeContent(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Content is required"));
        }
        String summary = aiService.generateSummary(content);
        return ResponseEntity.ok(Map.of("summary", summary));
    }
}
