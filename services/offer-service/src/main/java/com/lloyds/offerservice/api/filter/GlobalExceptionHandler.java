package com.lloyds.offerservice.api.filter;

import com.lloyds.offerservice.domain.exception.BusinessRuleException;
import com.lloyds.offerservice.domain.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/not-found"));
        problem.setTitle("Resource Not Found");
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        log.warn("Business rule violation [{}]: {}", ex.getRuleCode(), ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/business-rule-violation"));
        problem.setTitle("Business Rule Violation");
        problem.setProperty("ruleCode", ex.getRuleCode());
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.lloyds.com/errors/validation-failed"));
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields failed validation");
        List<Map<String, String>> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid"))
                .toList();
        problem.setProperty("violations", violations);
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
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
