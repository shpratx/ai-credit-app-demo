package com.lloyds.creditcoach.plan.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ImprovementPlan {

    private UUID id;
    private UUID customerId;
    private PlanStatus status;
    private Confidence confidence;
    private Integer scoreAtGeneration;
    private Instant generatedAt;
    private String disclaimer;

    public ImprovementPlan() {
        this.id = UUID.randomUUID();
    }

    public enum PlanStatus { ACTIVE, GENERATING, EXPIRED, NO_ACTIONS_NEEDED }
    public enum Confidence { HIGH, MEDIUM, LOW }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public PlanStatus getStatus() { return status; }
    public void setStatus(PlanStatus status) { this.status = status; }
    public Confidence getConfidence() { return confidence; }
    public void setConfidence(Confidence confidence) { this.confidence = confidence; }
    public Integer getScoreAtGeneration() { return scoreAtGeneration; }
    public void setScoreAtGeneration(Integer scoreAtGeneration) { this.scoreAtGeneration = scoreAtGeneration; }
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
