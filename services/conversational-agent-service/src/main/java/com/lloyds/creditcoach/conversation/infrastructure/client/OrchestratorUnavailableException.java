package com.lloyds.creditcoach.conversation.infrastructure.client;

public class OrchestratorUnavailableException extends RuntimeException {

    public OrchestratorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
