package com.coopcredit.riskcentral.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for risk evaluation request.
 */
public class RiskEvaluationRequest {

    @NotBlank(message = "Documento is required")
    private String documento;

    @NotNull(message = "Monto is required")
    private BigDecimal monto;

    @NotNull(message = "Plazo is required")
    private Integer plazo;

    public RiskEvaluationRequest() {
    }

    public RiskEvaluationRequest(String documento, BigDecimal monto, Integer plazo) {
        this.documento = documento;
        this.monto = monto;
        this.plazo = plazo;
    }

    // Getters and Setters

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }
}
