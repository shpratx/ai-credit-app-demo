package com.lloyds.simulationservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "simulations")
@EntityListeners(AuditingEntityListener.class)
public class SimulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scenario_type", nullable = false, length = 30)
    private com.lloyds.simulationservice.domain.model.ScenarioType scenarioType;

    @Column(name = "current_score", nullable = false)
    private int currentScore;

    @Column(name = "estimated_score", nullable = false)
    private int estimatedScore;

    @Column(name = "point_impact", nullable = false)
    private int pointImpact;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence", nullable = false, length = 10)
    private com.lloyds.simulationservice.domain.model.Confidence confidence;

    @Column(name = "factors_changed", columnDefinition = "TEXT")
    private String factorsChanged;

    @Column(name = "disclaimer", columnDefinition = "TEXT")
    private String disclaimer;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public com.lloyds.simulationservice.domain.model.ScenarioType getScenarioType() { return scenarioType; }
    public void setScenarioType(com.lloyds.simulationservice.domain.model.ScenarioType scenarioType) { this.scenarioType = scenarioType; }
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }
    public int getEstimatedScore() { return estimatedScore; }
    public void setEstimatedScore(int estimatedScore) { this.estimatedScore = estimatedScore; }
    public int getPointImpact() { return pointImpact; }
    public void setPointImpact(int pointImpact) { this.pointImpact = pointImpact; }
    public com.lloyds.simulationservice.domain.model.Confidence getConfidence() { return confidence; }
    public void setConfidence(com.lloyds.simulationservice.domain.model.Confidence confidence) { this.confidence = confidence; }
    public String getFactorsChanged() { return factorsChanged; }
    public void setFactorsChanged(String factorsChanged) { this.factorsChanged = factorsChanged; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
