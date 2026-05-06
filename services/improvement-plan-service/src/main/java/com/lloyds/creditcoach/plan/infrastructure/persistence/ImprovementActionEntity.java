package com.lloyds.creditcoach.plan.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "improvement_actions")
@EntityListeners(AuditingEntityListener.class)
public class ImprovementActionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_point_impact")
    private Integer estimatedPointImpact;

    @Column(name = "estimated_timeframe", length = 50)
    private String estimatedTimeframe;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getEstimatedPointImpact() { return estimatedPointImpact; }
    public void setEstimatedPointImpact(Integer estimatedPointImpact) { this.estimatedPointImpact = estimatedPointImpact; }
    public String getEstimatedTimeframe() { return estimatedTimeframe; }
    public void setEstimatedTimeframe(String estimatedTimeframe) { this.estimatedTimeframe = estimatedTimeframe; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
