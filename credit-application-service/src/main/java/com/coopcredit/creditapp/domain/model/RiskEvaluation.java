package com.coopcredit.creditapp.domain.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing a risk evaluation from the external risk central
 * service.
 * This is a pure domain object with no framework dependencies.
 */
public class RiskEvaluation {

    private Long id;
    private CreditApplication creditApplication;
    private String document;
    private Integer score;
    private RiskLevel riskLevel;
    private String detail;
    private LocalDateTime evaluationDate;

    public RiskEvaluation() {
        this.evaluationDate = LocalDateTime.now();
    }

    public RiskEvaluation(String document, Integer score, RiskLevel riskLevel, String detail) {
        this();
        this.document = document;
        this.score = score;
        this.riskLevel = riskLevel;
        this.detail = detail;
    }

    // Business logic methods

    public boolean isHighRisk() {
        return this.riskLevel == RiskLevel.HIGH;
    }

    public boolean isMediumRisk() {
        return this.riskLevel == RiskLevel.MEDIUM;
    }

    public boolean isLowRisk() {
        return this.riskLevel == RiskLevel.LOW;
    }

    public boolean isAcceptableRisk() {
        return this.riskLevel == RiskLevel.LOW || this.riskLevel == RiskLevel.MEDIUM;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplication getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplication creditApplication) {
        this.creditApplication = creditApplication;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }
}
