package com.coopcredit.riskcentral.dto;

/**
 * DTO for risk evaluation response.
 */
public class RiskEvaluationResponse {

    private String documento;
    private Integer score;
    private String nivelRiesgo;
    private String detalle;

    public RiskEvaluationResponse() {
    }

    public RiskEvaluationResponse(String documento, Integer score, String nivelRiesgo, String detalle) {
        this.documento = documento;
        this.score = score;
        this.nivelRiesgo = nivelRiesgo;
        this.detalle = detalle;
    }

    // Getters and Setters

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
