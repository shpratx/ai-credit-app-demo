package com.lloyds.creditcoach.conversation.application.command;

import com.lloyds.creditcoach.conversation.application.dto.ConversationRequest;
import com.lloyds.creditcoach.conversation.application.dto.ConversationResponse;
import com.lloyds.creditcoach.conversation.application.dto.SuggestedAction;
import com.lloyds.creditcoach.conversation.infrastructure.client.EnvoyOrchestratorClient;
import com.lloyds.creditcoach.conversation.infrastructure.client.IntentClassification;
import com.lloyds.creditcoach.conversation.infrastructure.client.VertexAiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendConversationalQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(SendConversationalQueryHandler.class);
    private static final double CONFIDENCE_THRESHOLD = 0.85;

    private final EnvoyOrchestratorClient orchestratorClient;
    private final VertexAiClient vertexAiClient;

    public SendConversationalQueryHandler(EnvoyOrchestratorClient orchestratorClient, VertexAiClient vertexAiClient) {
        this.orchestratorClient = orchestratorClient;
        this.vertexAiClient = vertexAiClient;
    }

    public ConversationResponse handle(ConversationRequest request) {
        log.info("Processing conversational query: sessionId={}", request.sessionId());

        // Route through Envoy orchestrator
        orchestratorClient.routeQuery(request.message(), request.sessionId());

        // Classify intent
        IntentClassification classification = vertexAiClient.classifyIntent(request.message());

        // If confidence below threshold, ask for clarification
        if (classification.confidence() < CONFIDENCE_THRESHOLD) {
            return new ConversationResponse(
                    "I'm not quite sure what you're asking. Could you rephrase your question about your credit?",
                    classification.intent(),
                    classification.confidence(),
                    List.of(
                            new SuggestedAction("What's my score?", "query:score_query"),
                            new SuggestedAction("How to improve?", "query:how_improve")
                    ),
                    null
            );
        }

        // Generate response
        String responseText = vertexAiClient.generateResponse(request.message(), classification.intent(), request.sessionId());

        // Build suggested actions based on intent
        List<SuggestedAction> actions = buildSuggestedActions(classification.intent());

        // Add disclaimer for financial estimates
        String disclaimer = classification.intent().contains("improvement")
                ? "This is general guidance, not financial advice. Results may vary."
                : null;

        return new ConversationResponse(
                responseText,
                classification.intent(),
                classification.confidence(),
                actions,
                disclaimer
        );
    }

    private List<SuggestedAction> buildSuggestedActions(String intent) {
        return switch (intent) {
            case "score_query" -> List.of(
                    new SuggestedAction("Why did it change?", "query:why_changed"),
                    new SuggestedAction("How to improve?", "query:how_improve")
            );
            case "improvement_query" -> List.of(
                    new SuggestedAction("Show my factors", "query:factors"),
                    new SuggestedAction("Score history", "query:history")
            );
            default -> List.of(
                    new SuggestedAction("What's my score?", "query:score_query")
            );
        };
    }
}
