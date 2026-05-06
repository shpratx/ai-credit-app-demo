package com.lloyds.creditcoach.compliance.domain.model;

import java.time.Instant;
import java.util.UUID;

public class DsarExport {
    private UUID id;
    private UUID customerId;
    private DsarStatus status;
    private Instant requestedAt;
    private Instant completedAt;
    private String downloadUrl;

    public enum DsarStatus { REQUESTED, PROCESSING, READY, DOWNLOADED }

    public DsarExport() {
        this.id = UUID.randomUUID();
        this.status = DsarStatus.REQUESTED;
        this.requestedAt = Instant.now();
    }

    public void markReady(String downloadUrl) {
        this.status = DsarStatus.READY;
        this.completedAt = Instant.now();
        this.downloadUrl = downloadUrl;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public DsarStatus getStatus() { return status; }
    public void setStatus(DsarStatus status) { this.status = status; }
    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
