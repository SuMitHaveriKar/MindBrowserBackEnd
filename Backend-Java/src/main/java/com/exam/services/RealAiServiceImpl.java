package com.exam.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class RealAiServiceImpl implements AiService {

    @Override
    public String generateContent(String prompt) {
        // TODO: Implement actual AI integration here (e.g., OpenAI or DeepSeek API
        // call)
        throw new UnsupportedOperationException("Real AI service is not implemented yet!");
    }

    @Override
    public String generateSummary(String content) {
        // TODO: Implement actual AI integration here
        throw new UnsupportedOperationException("Real AI service is not implemented yet!");
    }
}
