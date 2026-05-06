package com.lloyds.creditcoach.creditscore.infrastructure.client;

public class CraUnavailableException extends RuntimeException {

    public CraUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
