package com.lloyds.creditcoach.plan.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ImprovementAction {

    private UUID id;
    private UUID planId;
    private int rank;
    private String title;
    private String description;
    private Integer estimatedPointImpact;
    private String estimatedTimeframe;
    private ActionCategory category;
    private ActionStatus status;
    private Instant completedAt;
    private String explanation;

    public ImprovementAction() {
        this.id = UUID.randomUUID();
        this.status = ActionStatus.NOT_STARTED;
    }

    public enum ActionCategory {
        UTILISATION, PAYMENT_HISTORY, CREDIT_AGE, CREDIT_MIX, NEW_CREDIT, CREDIT_BUILDING
    }

    public enum ActionStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, DISMISSED }

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
    public ActionCategory getCategory() { return category; }
    public void setCategory(ActionCategory category) { this.category = category; }
    public ActionStatus getStatus() { return status; }
    public void setStatus(ActionStatus status) { this.status = status; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
