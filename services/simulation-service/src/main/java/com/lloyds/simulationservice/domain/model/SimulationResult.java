package com.lloyds.simulationservice.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SimulationResult {

    private UUID id;
    private UUID customerId;
    private ScenarioType scenarioType;
    private int currentScore;
    private int estimatedScore;
    private int pointImpact;
    private Confidence confidence;
    private List<String> factorsChanged;
    private String disclaimer;
    private Instant createdAt;

    public SimulationResult() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public ScenarioType getScenarioType() { return scenarioType; }
    public void setScenarioType(ScenarioType scenarioType) { this.scenarioType = scenarioType; }
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }
    public int getEstimatedScore() { return estimatedScore; }
    public void setEstimatedScore(int estimatedScore) { this.estimatedScore = estimatedScore; }
    public int getPointImpact() { return pointImpact; }
    public void setPointImpact(int pointImpact) { this.pointImpact = pointImpact; }
    public Confidence getConfidence() { return confidence; }
    public void setConfidence(Confidence confidence) { this.confidence = confidence; }
    public List<String> getFactorsChanged() { return factorsChanged; }
    public void setFactorsChanged(List<String> factorsChanged) { this.factorsChanged = factorsChanged; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
