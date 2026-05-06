package com.lloyds.creditcoach.alert.domain.model;

import java.util.UUID;

public class AlertPreference {
    private UUID id;
    private UUID customerId;
    private boolean utilisationEnabled;
    private int utilisationThreshold;
    private boolean paymentEnabled;
    private boolean eligibilityEnabled;
    private boolean scoreChangeEnabled;
    private boolean allDisabled;

    public AlertPreference() {
        this.id = UUID.randomUUID();
        this.utilisationEnabled = true;
        this.utilisationThreshold = 75;
        this.paymentEnabled = true;
        this.eligibilityEnabled = true;
        this.scoreChangeEnabled = true;
        this.allDisabled = false;
    }

    public boolean isAlertTypeEnabled(Alert.AlertType type) {
        if (allDisabled) return false;
        return switch (type) {
            case UTILISATION_WARNING -> utilisationEnabled;
            case PAYMENT_RISK -> paymentEnabled;
            case PRODUCT_ELIGIBILITY -> eligibilityEnabled;
            case SCORE_CHANGE -> scoreChangeEnabled;
        };
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public boolean isUtilisationEnabled() { return utilisationEnabled; }
    public void setUtilisationEnabled(boolean utilisationEnabled) { this.utilisationEnabled = utilisationEnabled; }
    public int getUtilisationThreshold() { return utilisationThreshold; }
    public void setUtilisationThreshold(int utilisationThreshold) { this.utilisationThreshold = utilisationThreshold; }
    public boolean isPaymentEnabled() { return paymentEnabled; }
    public void setPaymentEnabled(boolean paymentEnabled) { this.paymentEnabled = paymentEnabled; }
    public boolean isEligibilityEnabled() { return eligibilityEnabled; }
    public void setEligibilityEnabled(boolean eligibilityEnabled) { this.eligibilityEnabled = eligibilityEnabled; }
    public boolean isScoreChangeEnabled() { return scoreChangeEnabled; }
    public void setScoreChangeEnabled(boolean scoreChangeEnabled) { this.scoreChangeEnabled = scoreChangeEnabled; }
    public boolean isAllDisabled() { return allDisabled; }
    public void setAllDisabled(boolean allDisabled) { this.allDisabled = allDisabled; }
}
