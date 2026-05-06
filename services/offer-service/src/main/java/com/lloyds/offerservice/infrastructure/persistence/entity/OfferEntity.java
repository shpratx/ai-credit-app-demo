package com.lloyds.offerservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pre_approved_offers")
@EntityListeners(AuditingEntityListener.class)
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal rate;

    @Column(name = "apr", nullable = false, precision = 5, scale = 2)
    private BigDecimal apr;

    @Column(name = "term", nullable = false)
    private int term;

    @Column(name = "monthly_payment", nullable = false, precision = 18, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "total_payable", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPayable;

    @Column(name = "total_charge_for_credit", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalChargeForCredit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private com.lloyds.offerservice.domain.model.OfferStatus status;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Getters and setters
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
    public com.lloyds.offerservice.domain.model.OfferStatus getStatus() { return status; }
    public void setStatus(com.lloyds.offerservice.domain.model.OfferStatus status) { this.status = status; }
    public Instant getValidUntil() { return validUntil; }
    public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
