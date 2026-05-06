package com.lloyds.creditcoach.conversation.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VertexAiClient {

    private static final Logger log = LoggerFactory.getLogger(VertexAiClient.class);

    @CircuitBreaker(name = "vertexai", fallbackMethod = "fallback")
    public IntentClassification classifyIntent(String message) {
        log.info("Classifying intent via Vertex AI");
        // In production: call Vertex AI Gemini endpoint
        // Placeholder for compilation
        if (message.toLowerCase().contains("score")) {
            return new IntentClassification("score_query", 0.96);
        }
        if (message.toLowerCase().contains("improve")) {
            return new IntentClassification("improvement_query", 0.91);
        }
        return new IntentClassification("general_query", 0.75);
    }

    @CircuitBreaker(name = "vertexai", fallbackMethod = "generateFallback")
    public String generateResponse(String message, String intent, String sessionId) {
        log.info("Generating response via Vertex AI for intent={}", intent);
        // In production: call Vertex AI with prompt template
        return "Based on your credit profile, here's what I can tell you about your " + intent + ".";
    }

    private IntentClassification fallback(String message, Throwable t) {
        log.error("Vertex AI unavailable for classification", t);
        throw new OrchestratorUnavailableException("Vertex AI unavailable", t);
    }

    private String generateFallback(String message, String intent, String sessionId, Throwable t) {
        log.error("Vertex AI unavailable for generation", t);
        throw new OrchestratorUnavailableException("Vertex AI unavailable", t);
    }
}
