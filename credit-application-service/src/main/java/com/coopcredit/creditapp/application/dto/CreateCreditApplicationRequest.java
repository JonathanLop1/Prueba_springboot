package com.coopcredit.creditapp.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for creating a new credit application.
 */
public class CreateCreditApplicationRequest {

    @NotNull(message = "Affiliate ID is required")
    private Long affiliateId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "100.00", message = "Minimum credit amount is 100")
    @DecimalMax(value = "1000000000.00", message = "Maximum credit amount is 1,000,000,000")
    private BigDecimal requestedAmount;

    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Minimum term is 1 month")
    @Max(value = 360, message = "Maximum term is 360 months")
    private Integer termMonths;

    @NotNull(message = "Proposed rate is required")
    @DecimalMin(value = "0.01", message = "Rate must be at least 0.01%")
    @DecimalMax(value = "100.00", message = "Rate cannot exceed 100%")
    private BigDecimal proposedRate;

    public CreateCreditApplicationRequest() {
    }

    public CreateCreditApplicationRequest(Long affiliateId, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate) {
        this.affiliateId = affiliateId;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.proposedRate = proposedRate;
    }

    // Getters and Setters

    public Long getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
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
}
