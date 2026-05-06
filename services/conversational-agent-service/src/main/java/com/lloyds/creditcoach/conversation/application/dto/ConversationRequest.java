package com.lloyds.creditcoach.conversation.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConversationRequest(
        @NotBlank @Size(max = 500) String message,
        @NotNull String sessionId,
        ConversationContext context
) {}
