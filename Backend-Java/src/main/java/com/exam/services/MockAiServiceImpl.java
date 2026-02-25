package com.exam.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockAiServiceImpl implements AiService {

    @Override
    public String generateContent(String prompt) {
        return "This is a mocked AI response for the prompt: " + prompt + ".\n\n" +
                "It provides a fast, simulated answer without calling any real external API, " +
                "saving costs and improving development speed.";
    }

    @Override
    public String generateSummary(String content) {
        return "This is a mocked summary of the provided content. The content length was " + content.length()
                + " characters.";
    }
}
