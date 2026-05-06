package com.lloyds.creditcoach.consent.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_coach_consents")
@EntityListeners(AuditingEntityListener.class)
public class ConsentEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "cra_provider", nullable = false, length = 20)
    private String craProvider;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "consent_text_version", nullable = false, length = 10)
    private String consentTextVersion;

    @Column(name = "consent_text_hash", nullable = false, length = 64)
    private String consentTextHash;

    @Column(name = "granted_at")
    private Instant grantedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    @Column(name = "channel", nullable = false, length = 10)
    private String channel;

    @Column(name = "ip_address", nullable = false, length = 512)
    private String ipAddress;

    @Column(name = "device_fingerprint", nullable = false, length = 512)
    private String deviceFingerprint;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCraProvider() { return craProvider; }
    public void setCraProvider(String craProvider) { this.craProvider = craProvider; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getConsentTextVersion() { return consentTextVersion; }
    public void setConsentTextVersion(String consentTextVersion) { this.consentTextVersion = consentTextVersion; }
    public String getConsentTextHash() { return consentTextHash; }
    public void setConsentTextHash(String consentTextHash) { this.consentTextHash = consentTextHash; }
    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }
    public Instant getWithdrawnAt() { return withdrawnAt; }
    public void setWithdrawnAt(Instant withdrawnAt) { this.withdrawnAt = withdrawnAt; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
