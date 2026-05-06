package com.lloyds.creditcoach.compliance.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class BreathingSpace {
    private UUID id;
    private UUID customerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BreathingSpaceStatus status;
    private Instant notifiedAt;

    public enum BreathingSpaceStatus { ACTIVE, EXPIRED }

    public static BreathingSpace activate(UUID customerId) {
        var bs = new BreathingSpace();
        bs.id = UUID.randomUUID();
        bs.customerId = customerId;
        bs.startDate = LocalDate.now();
        bs.endDate = LocalDate.now().plusDays(60);
        bs.status = BreathingSpaceStatus.ACTIVE;
        bs.notifiedAt = Instant.now();
        return bs;
    }

    public boolean isActive() {
        return status == BreathingSpaceStatus.ACTIVE && !LocalDate.now().isAfter(endDate);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BreathingSpaceStatus getStatus() { return status; }
    public void setStatus(BreathingSpaceStatus status) { this.status = status; }
    public Instant getNotifiedAt() { return notifiedAt; }
    public void setNotifiedAt(Instant notifiedAt) { this.notifiedAt = notifiedAt; }
}
