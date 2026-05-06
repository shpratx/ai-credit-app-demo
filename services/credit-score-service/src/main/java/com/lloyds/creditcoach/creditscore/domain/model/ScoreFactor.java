package com.lloyds.creditcoach.creditscore.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ScoreFactor {

    private UUID id;
    private UUID scoreId;
    private String category;
    private String impact;
    private String direction;
    private String title;
    private String description;
    private Integer weightingPercent;
    private Instant createdAt;

    public ScoreFactor() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

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
