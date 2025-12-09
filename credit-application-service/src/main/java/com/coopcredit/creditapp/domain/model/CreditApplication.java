package com.coopcredit.creditapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing a credit application.
 * This is a pure domain object with no framework dependencies.
 */
public class CreditApplication {

    private Long id;
    private Affiliate affiliate;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal proposedRate;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private RiskEvaluation riskEvaluation;
    private String rejectionReason;

    public CreditApplication() {
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    public CreditApplication(Affiliate affiliate, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate) {
        this();
        this.affiliate = affiliate;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.proposedRate = proposedRate;
    }

    // Business logic methods

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ApplicationStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }

    public void setRiskEvaluation(RiskEvaluation evaluation) {
        this.riskEvaluation = evaluation;
    }

    public BigDecimal calculateMonthlyPayment() {
        if (requestedAmount == null || termMonths == null || proposedRate == null) {
            return BigDecimal.ZERO;
        }

        // Simple calculation: (amount * (1 + rate)) / months
        BigDecimal totalWithInterest = requestedAmount.multiply(
                BigDecimal.ONE.add(proposedRate.divide(BigDecimal.valueOf(100))));
        return totalWithInterest.divide(BigDecimal.valueOf(termMonths), 2, BigDecimal.ROUND_HALF_UP);
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Affiliate getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Affiliate affiliate) {
        this.affiliate = affiliate;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(BigDecimal proposedRate) {
        this.proposedRate = proposedRate;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public RiskEvaluation getRiskEvaluation() {
        return riskEvaluation;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
