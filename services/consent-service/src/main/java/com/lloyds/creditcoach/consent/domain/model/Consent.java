package com.lloyds.creditcoach.consent.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Consent {

    private UUID id;
    private UUID customerId;
    private String craProvider;
    private String status;
    private String consentTextVersion;
    private String consentTextHash;
    private Instant grantedAt;
    private Instant withdrawnAt;
    private String channel;
    private String ipAddress;
    private String deviceFingerprint;
    private Instant createdAt;

    public Consent() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public static Consent grant(UUID customerId, String craProvider, String consentTextVersion,
                                String consentTextHash, String channel, String ipAddress, String deviceFingerprint) {
        var consent = new Consent();
        consent.customerId = customerId;
        consent.craProvider = craProvider;
        consent.status = "GRANTED";
        consent.consentTextVersion = consentTextVersion;
        consent.consentTextHash = consentTextHash;
        consent.grantedAt = Instant.now();
        consent.channel = channel;
        consent.ipAddress = ipAddress;
        consent.deviceFingerprint = deviceFingerprint;
        return consent;
    }

    public Consent withdraw() {
        var withdrawal = new Consent();
        withdrawal.customerId = this.customerId;
        withdrawal.craProvider = this.craProvider;
        withdrawal.status = "WITHDRAWN";
        withdrawal.consentTextVersion = this.consentTextVersion;
        withdrawal.consentTextHash = this.consentTextHash;
        withdrawal.grantedAt = this.grantedAt;
        withdrawal.withdrawnAt = Instant.now();
        withdrawal.channel = this.channel;
        withdrawal.ipAddress = this.ipAddress;
        withdrawal.deviceFingerprint = this.deviceFingerprint;
        return withdrawal;
    }

    public boolean isGranted() {
        return "GRANTED".equals(status);
    }

    public boolean isWithdrawn() {
        return "WITHDRAWN".equals(status);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getCraProvider() { return craProvider; }
    public String getStatus() { return status; }
    public String getConsentTextVersion() { return consentTextVersion; }
    public String getConsentTextHash() { return consentTextHash; }
    public Instant getGrantedAt() { return grantedAt; }
    public Instant getWithdrawnAt() { return withdrawnAt; }
    public String getChannel() { return channel; }
    public String getIpAddress() { return ipAddress; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setCraProvider(String craProvider) { this.craProvider = craProvider; }
    public void setStatus(String status) { this.status = status; }
    public void setConsentTextVersion(String consentTextVersion) { this.consentTextVersion = consentTextVersion; }
    public void setConsentTextHash(String consentTextHash) { this.consentTextHash = consentTextHash; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }
    public void setWithdrawnAt(Instant withdrawnAt) { this.withdrawnAt = withdrawnAt; }
    public void setChannel(String channel) { this.channel = channel; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
