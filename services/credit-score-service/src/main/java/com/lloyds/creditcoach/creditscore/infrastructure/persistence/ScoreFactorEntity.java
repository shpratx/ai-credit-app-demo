package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_score_factors")
@EntityListeners(AuditingEntityListener.class)
public class ScoreFactorEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "score_id", nullable = false)
    private UUID scoreId;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "impact", nullable = false, length = 10)
    private String impact;

    @Column(name = "direction", nullable = false, length = 10)
    private String direction;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "weighting_percent")
    private Integer weightingPercent;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getScoreId() { return scoreId; }
    public void setScoreId(UUID scoreId) { this.scoreId = scoreId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getWeightingPercent() { return weightingPercent; }
    public void setWeightingPercent(Integer weightingPercent) { this.weightingPercent = weightingPercent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
