package com.lloyds.creditcoach.conversation.infrastructure.client;

public record OrchestratorResponse(String processedMessage, String sessionId, boolean routed) {}
