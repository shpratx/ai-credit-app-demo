package com.lloyds.creditcoach.conversation.unit;

import com.lloyds.creditcoach.conversation.application.command.SendConversationalQueryHandler;
import com.lloyds.creditcoach.conversation.application.dto.ConversationContext;
import com.lloyds.creditcoach.conversation.application.dto.ConversationRequest;
import com.lloyds.creditcoach.conversation.application.dto.ConversationResponse;
import com.lloyds.creditcoach.conversation.infrastructure.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendConversationalQueryHandlerTest {

    @Mock
    private EnvoyOrchestratorClient orchestratorClient;
    @Mock
    private VertexAiClient vertexAiClient;

    private SendConversationalQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SendConversationalQueryHandler(orchestratorClient, vertexAiClient);
    }

    @Test
    void should_returnResponse_when_highConfidenceIntent() {
        var request = new ConversationRequest("How's my credit score?", "session-1", new ConversationContext("/dashboard"));

        when(orchestratorClient.routeQuery(any(), any()))
                .thenReturn(new OrchestratorResponse("How's my credit score?", "session-1", true));
        when(vertexAiClient.classifyIntent("How's my credit score?"))
                .thenReturn(new IntentClassification("score_query", 0.96));
        when(vertexAiClient.generateResponse(eq("How's my credit score?"), eq("score_query"), eq("session-1")))
                .thenReturn("Your score is 742 (Good).");

        ConversationResponse response = handler.handle(request);

        assertThat(response.intent()).isEqualTo("score_query");
        assertThat(response.confidence()).isEqualTo(0.96);
        assertThat(response.responseText()).contains("742");
        assertThat(response.suggestedActions()).isNotEmpty();
    }

    @Test
    void should_askClarification_when_lowConfidence() {
        var request = new ConversationRequest("hmm something", "session-1", null);

        when(orchestratorClient.routeQuery(any(), any()))
                .thenReturn(new OrchestratorResponse("hmm something", "session-1", true));
        when(vertexAiClient.classifyIntent("hmm something"))
                .thenReturn(new IntentClassification("general_query", 0.45));

        ConversationResponse response = handler.handle(request);

        assertThat(response.confidence()).isLessThan(0.85);
        assertThat(response.responseText()).contains("rephrase");
    }
}
