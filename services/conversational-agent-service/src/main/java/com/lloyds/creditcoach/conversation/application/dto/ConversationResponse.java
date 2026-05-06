package com.lloyds.creditcoach.conversation.application.dto;

import java.util.List;

public record ConversationResponse(
        String responseText,
        String intent,
        double confidence,
        List<SuggestedAction> suggestedActions,
        String disclaimer
) {}
