package com.lloyds.offerservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "offer_audit_entries")
@EntityListeners(AuditingEntityListener.class)
public class OfferAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private com.lloyds.offerservice.domain.model.AuditAction action;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "reason")
    private String reason;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOfferId() { return offerId; }
    public void setOfferId(UUID offerId) { this.offerId = offerId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public com.lloyds.offerservice.domain.model.AuditAction getAction() { return action; }
    public void setAction(com.lloyds.offerservice.domain.model.AuditAction action) { this.action = action; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
