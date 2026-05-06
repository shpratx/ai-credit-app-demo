package com.lloyds.creditcoach.plan.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Milestone {

    private UUID id;
    private UUID customerId;
    private MilestoneType type;
    private String title;
    private String description;
    private Instant achievedAt;
    private Integer scoreAtAchievement;
    private Integer targetScore;

    public Milestone() {
        this.id = UUID.randomUUID();
    }

    public enum MilestoneType { SCORE_THRESHOLD, ACTIONS_COMPLETED, STREAK, FIRST_IMPROVEMENT }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public MilestoneType getType() { return type; }
    public void setType(MilestoneType type) { this.type = type; }
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
}
