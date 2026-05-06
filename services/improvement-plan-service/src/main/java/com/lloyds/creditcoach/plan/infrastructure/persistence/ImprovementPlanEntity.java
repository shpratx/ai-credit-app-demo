package com.lloyds.creditcoach.plan.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "improvement_plans")
@EntityListeners(AuditingEntityListener.class)
public class ImprovementPlanEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "confidence", length = 10)
    private String confidence;

    @Column(name = "score_at_generation")
    private Integer scoreAtGeneration;

    @Column(name = "generated_at")
    private Instant generatedAt;

    @Column(name = "disclaimer", length = 500)
    private String disclaimer;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public Integer getScoreAtGeneration() { return scoreAtGeneration; }
    public void setScoreAtGeneration(Integer scoreAtGeneration) { this.scoreAtGeneration = scoreAtGeneration; }
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
