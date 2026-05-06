package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dsar_exports")
public class DsarExportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "download_url")
    private String downloadUrl;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
