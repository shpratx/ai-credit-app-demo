package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cra_api_audit_log")
@EntityListeners(AuditingEntityListener.class)
public class CraApiAuditLogEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(name = "response_status", nullable = false, length = 20)
    private String responseStatus;

    @Column(name = "latency_ms", nullable = false)
    private int latencyMs;

    @Column(name = "circuit_breaker_state", nullable = false, length = 20)
    private String circuitBreakerState;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

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
