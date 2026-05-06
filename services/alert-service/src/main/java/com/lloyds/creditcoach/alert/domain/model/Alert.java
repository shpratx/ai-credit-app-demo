package com.lloyds.creditcoach.alert.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Alert {
    private UUID id;
    private UUID customerId;
    private AlertType type;
    private String title;
    private String message;
    private Severity severity;
    private AlertStatus status;
    private Instant createdAt;

    public enum AlertType { UTILISATION_WARNING, PAYMENT_RISK, PRODUCT_ELIGIBILITY, SCORE_CHANGE }
    public enum Severity { HIGH, MEDIUM, LOW }
    public enum AlertStatus { UNREAD, READ, DISMISSED }

    public Alert() {
        this.id = UUID.randomUUID();
        this.status = AlertStatus.UNREAD;
        this.createdAt = Instant.now();
    }

    public static Alert create(UUID customerId, AlertType type, String title, String message, Severity severity) {
        var alert = new Alert();
        alert.customerId = customerId;
        alert.type = type;
        alert.title = title;
        alert.message = message;
        alert.severity = severity;
        return alert;
    }

    public void dismiss() { this.status = AlertStatus.DISMISSED; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public AlertType getType() { return type; }
    public void setType(AlertType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
