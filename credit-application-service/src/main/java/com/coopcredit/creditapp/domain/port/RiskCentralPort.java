package com.coopcredit.creditapp.domain.port;

import java.math.BigDecimal;

/**
 * Port interface for external risk evaluation service.
 * This represents the output port to the Risk Central service.
 */
public interface RiskCentralPort {

    /**
     * Evaluates risk for a given document, amount, and term.
     *
     * @param document   The affiliate's document number
     * @param amount     The requested credit amount
     * @param termMonths The requested term in months
     * @return RiskEvaluationResponse containing score, level, and details
     */
    RiskEvaluationResponse evaluateRisk(String document, BigDecimal amount, Integer termMonths);

    /**
     * Response DTO from the risk central service.
     */
    class RiskEvaluationResponse {
        private String document;
        private Integer score;
        private String riskLevel;
        private String detail;

        public RiskEvaluationResponse() {
        }

        public RiskEvaluationResponse(String document, Integer score, String riskLevel, String detail) {
            this.document = document;
            this.score = score;
            this.riskLevel = riskLevel;
            this.detail = detail;
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

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
