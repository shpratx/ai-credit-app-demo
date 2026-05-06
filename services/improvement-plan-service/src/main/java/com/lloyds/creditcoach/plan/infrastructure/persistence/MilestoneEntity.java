package com.lloyds.creditcoach.plan.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "milestones")
@EntityListeners(AuditingEntityListener.class)
public class MilestoneEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "achieved_at")
    private Instant achievedAt;

    @Column(name = "score_at_achievement")
    private Integer scoreAtAchievement;

    @Column(name = "target_score")
    private Integer targetScore;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getAchievedAt() { return achievedAt; }
    public void setAchievedAt(Instant achievedAt) { this.achievedAt = achievedAt; }
    public Integer getScoreAtAchievement() { return scoreAtAchievement; }
    public void setScoreAtAchievement(Integer scoreAtAchievement) { this.scoreAtAchievement = scoreAtAchievement; }
    public Integer getTargetScore() { return targetScore; }
    public void setTargetScore(Integer targetScore) { this.targetScore = targetScore; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
