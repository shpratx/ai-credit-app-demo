package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "breathing_spaces")
public class BreathingSpaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(name = "notified_at")
    private Instant notifiedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getNotifiedAt() { return notifiedAt; }
    public void setNotifiedAt(Instant notifiedAt) { this.notifiedAt = notifiedAt; }
}
