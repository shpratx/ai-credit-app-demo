package com.lloyds.creditcoach.plan.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleNotFound(IllegalArgumentException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/not-found"));
        problem.setTitle("Resource Not Found");
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setType(URI.create("https://api.lloyds.com/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        enrich(problem);
        return problem;
    }

    private void enrich(ProblemDetail problem) {
        problem.setProperty("correlationId", MDC.get("correlationId"));
        problem.setProperty("timestamp", Instant.now().toString());
    }
}
