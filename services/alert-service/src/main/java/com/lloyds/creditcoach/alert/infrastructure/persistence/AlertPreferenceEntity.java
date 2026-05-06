package com.lloyds.creditcoach.alert.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "alert_preferences")
public class AlertPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "utilisation_enabled", nullable = false)
    private boolean utilisationEnabled = true;

    @Column(name = "utilisation_threshold", nullable = false)
    private int utilisationThreshold = 75;

    @Column(name = "payment_enabled", nullable = false)
    private boolean paymentEnabled = true;

    @Column(name = "eligibility_enabled", nullable = false)
    private boolean eligibilityEnabled = true;

    @Column(name = "score_change_enabled", nullable = false)
    private boolean scoreChangeEnabled = true;

    @Column(name = "all_disabled", nullable = false)
    private boolean allDisabled = false;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public boolean isUtilisationEnabled() { return utilisationEnabled; }
    public void setUtilisationEnabled(boolean v) { this.utilisationEnabled = v; }
    public int getUtilisationThreshold() { return utilisationThreshold; }
    public void setUtilisationThreshold(int v) { this.utilisationThreshold = v; }
    public boolean isPaymentEnabled() { return paymentEnabled; }
    public void setPaymentEnabled(boolean v) { this.paymentEnabled = v; }
    public boolean isEligibilityEnabled() { return eligibilityEnabled; }
    public void setEligibilityEnabled(boolean v) { this.eligibilityEnabled = v; }
    public boolean isScoreChangeEnabled() { return scoreChangeEnabled; }
    public void setScoreChangeEnabled(boolean v) { this.scoreChangeEnabled = v; }
    public boolean isAllDisabled() { return allDisabled; }
    public void setAllDisabled(boolean v) { this.allDisabled = v; }
}
