package com.exam.services;

public interface AiService {
    String generateContent(String prompt);

    String generateSummary(String content);
}
