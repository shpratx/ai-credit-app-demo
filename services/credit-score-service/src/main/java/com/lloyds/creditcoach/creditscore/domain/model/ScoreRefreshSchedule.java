package com.lloyds.creditcoach.creditscore.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ScoreRefreshSchedule {

    private UUID id;
    private UUID customerId;
    private String provider;
    private int frequencyDays;
    private Instant lastRefreshedAt;
    private Instant nextRefreshAt;
    private String status;
    private int retryCount;
    private Instant createdAt;
    private Instant updatedAt;

    public ScoreRefreshSchedule() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public int getFrequencyDays() { return frequencyDays; }
    public void setFrequencyDays(int frequencyDays) { this.frequencyDays = frequencyDays; }
    public Instant getLastRefreshedAt() { return lastRefreshedAt; }
    public void setLastRefreshedAt(Instant lastRefreshedAt) { this.lastRefreshedAt = lastRefreshedAt; }
    public Instant getNextRefreshAt() { return nextRefreshAt; }
    public void setNextRefreshAt(Instant nextRefreshAt) { this.nextRefreshAt = nextRefreshAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
