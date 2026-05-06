package com.lloyds.offerservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PreApprovedOffer {

    private UUID id;
    private UUID customerId;
    private UUID productId;
    private BigDecimal amount;
    private BigDecimal rate;
    private BigDecimal apr;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal totalPayable;
    private BigDecimal totalChargeForCredit;
    private OfferStatus status;
    private Instant validUntil;
    private Instant createdAt;

    public PreApprovedOffer() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.status = OfferStatus.AVAILABLE;
    }

    public boolean isValid() {
        return status == OfferStatus.AVAILABLE && Instant.now().isBefore(validUntil);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public BigDecimal getApr() { return apr; }
    public void setApr(BigDecimal apr) { this.apr = apr; }
    public int getTerm() { return term; }
    public void setTerm(int term) { this.term = term; }
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    public BigDecimal getTotalPayable() { return totalPayable; }
    public void setTotalPayable(BigDecimal totalPayable) { this.totalPayable = totalPayable; }
    public BigDecimal getTotalChargeForCredit() { return totalChargeForCredit; }
    public void setTotalChargeForCredit(BigDecimal totalChargeForCredit) { this.totalChargeForCredit = totalChargeForCredit; }
    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }
    public Instant getValidUntil() { return validUntil; }
    public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
