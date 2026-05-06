package com.lloyds.offerservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class OfferAuditEntry {

    private UUID id;
    private UUID offerId;
    private UUID customerId;
    private AuditAction action;
    private Instant timestamp;
    private String reason;

    public OfferAuditEntry() {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
    }

    public static OfferAuditEntry create(UUID offerId, UUID customerId, AuditAction action, String reason) {
        var entry = new OfferAuditEntry();
        entry.setOfferId(offerId);
        entry.setCustomerId(customerId);
        entry.setAction(action);
        entry.setReason(reason);
        return entry;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOfferId() { return offerId; }
    public void setOfferId(UUID offerId) { this.offerId = offerId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
