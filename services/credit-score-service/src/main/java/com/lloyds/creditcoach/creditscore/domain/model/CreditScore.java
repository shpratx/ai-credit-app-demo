package com.lloyds.creditcoach.creditscore.domain.model;

import java.time.Instant;
import java.util.UUID;

public class CreditScore {

    private UUID id;
    private UUID customerId;
    private String provider;
    private byte[] scoreValue; // encrypted
    private int maxScore;
    private String band;
    private byte[] previousScore; // encrypted
    private Integer change;
    private String changeDirection;
    private Instant retrievedAt;
    private boolean isStale;
    private int dataQualityScore;
    private Instant createdAt;

    public CreditScore() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public static String classifyBand(int score) {
        if (score >= 961) return "excellent";
        if (score >= 881) return "very_good";
        if (score >= 721) return "good";
        if (score >= 561) return "fair";
        return "poor";
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public byte[] getScoreValue() { return scoreValue; }
    public void setScoreValue(byte[] scoreValue) { this.scoreValue = scoreValue; }
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    public String getBand() { return band; }
    public void setBand(String band) { this.band = band; }
    public byte[] getPreviousScore() { return previousScore; }
    public void setPreviousScore(byte[] previousScore) { this.previousScore = previousScore; }
    public Integer getChange() { return change; }
    public void setChange(Integer change) { this.change = change; }
    public String getChangeDirection() { return changeDirection; }
    public void setChangeDirection(String changeDirection) { this.changeDirection = changeDirection; }
    public Instant getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(Instant retrievedAt) { this.retrievedAt = retrievedAt; }
    public boolean isStale() { return isStale; }
    public void setStale(boolean stale) { isStale = stale; }
    public int getDataQualityScore() { return dataQualityScore; }
    public void setDataQualityScore(int dataQualityScore) { this.dataQualityScore = dataQualityScore; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
