package com.lloyds.creditcoach.creditscore.api.exception;

import com.lloyds.creditcoach.creditscore.infrastructure.client.CraUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problem.setType(URI.create("https://api.lloyds.com/problems/" + ex.getStatusCode().value()));
        return problem;
    }

    @ExceptionHandler(CraUnavailableException.class)
    public ProblemDetail handleCraUnavailable(CraUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/problems/cra-unavailable"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setType(URI.create("https://api.lloyds.com/problems/validation"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
