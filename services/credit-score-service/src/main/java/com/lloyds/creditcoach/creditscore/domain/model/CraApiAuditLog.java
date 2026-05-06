package com.lloyds.creditcoach.creditscore.domain.model;

import java.time.Instant;
import java.util.UUID;

public class CraApiAuditLog {

    private UUID id;
    private UUID customerId;
    private String provider;
    private String requestHash;
    private String responseStatus;
    private int latencyMs;
    private String circuitBreakerState;
    private UUID correlationId;
    private Instant createdAt;

    public CraApiAuditLog() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
    public String getResponseStatus() { return responseStatus; }
    public void setResponseStatus(String responseStatus) { this.responseStatus = responseStatus; }
    public int getLatencyMs() { return latencyMs; }
    public void setLatencyMs(int latencyMs) { this.latencyMs = latencyMs; }
    public String getCircuitBreakerState() { return circuitBreakerState; }
    public void setCircuitBreakerState(String circuitBreakerState) { this.circuitBreakerState = circuitBreakerState; }
    public UUID getCorrelationId() { return correlationId; }
    public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
