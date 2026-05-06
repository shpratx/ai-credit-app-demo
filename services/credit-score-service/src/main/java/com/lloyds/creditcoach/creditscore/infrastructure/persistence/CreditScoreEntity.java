package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_scores")
@EntityListeners(AuditingEntityListener.class)
public class CreditScoreEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "score_value", nullable = false)
    private byte[] scoreValue;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(name = "band", nullable = false, length = 20)
    private String band;

    @Column(name = "previous_score")
    private byte[] previousScore;

    @Column(name = "change")
    private Integer change;

    @Column(name = "change_direction", length = 10)
    private String changeDirection;

    @Column(name = "retrieved_at", nullable = false)
    private Instant retrievedAt;

    @Column(name = "is_stale", nullable = false)
    private boolean isStale;

    @Column(name = "data_quality_score", nullable = false)
    private int dataQualityScore;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

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
