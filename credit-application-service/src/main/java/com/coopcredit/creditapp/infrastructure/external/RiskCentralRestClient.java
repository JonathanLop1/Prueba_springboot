package com.coopcredit.creditapp.infrastructure.external;

import com.coopcredit.creditapp.domain.port.RiskCentralPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST client adapter for communicating with the Risk Central service.
 * This implements the RiskCentralPort output port.
 */
@Component
public class RiskCentralRestClient implements RiskCentralPort {

    private static final Logger log = LoggerFactory.getLogger(RiskCentralRestClient.class);

    private final RestTemplate restTemplate;
    private final String riskCentralUrl;

    public RiskCentralRestClient(RestTemplate restTemplate,
            @Value("${risk-central.url}") String riskCentralUrl) {
        this.restTemplate = restTemplate;
        this.riskCentralUrl = riskCentralUrl;
    }

    @Override
    public RiskEvaluationResponse evaluateRisk(String document, BigDecimal amount, Integer termMonths) {
        String url = riskCentralUrl + "/risk-evaluation";

        log.info("Calling Risk Central Service at: {}", url);

        // Prepare request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("documento", document);
        requestBody.put("monto", amount);
        requestBody.put("plazo", termMonths);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<RiskCentralResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    RiskCentralResponse.class);

            RiskCentralResponse responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Empty response from Risk Central Service");
            }

            log.info("Risk evaluation received: score={}, level={}",
                    responseBody.getScore(), responseBody.getNivelRiesgo());

            return new RiskEvaluationResponse(
                    responseBody.getDocumento(),
                    responseBody.getScore(),
                    mapRiskLevel(responseBody.getNivelRiesgo()),
                    responseBody.getDetalle());

        } catch (Exception e) {
            log.error("Error calling Risk Central Service", e);
            throw new RuntimeException("Failed to evaluate risk: " + e.getMessage(), e);
        }
    }

    private String mapRiskLevel(String nivelRiesgo) {
        if (nivelRiesgo == null) {
            return "MEDIUM";
        }

        return switch (nivelRiesgo.toUpperCase()) {
            case "BAJO" -> "LOW";
            case "MEDIO" -> "MEDIUM";
            case "ALTO" -> "HIGH";
            default -> nivelRiesgo.toUpperCase();
        };
    }

    /**
     * DTO matching the Risk Central Service response format.
     */
    private static class RiskCentralResponse {
        private String documento;
        private Integer score;
        private String nivelRiesgo;
        private String detalle;

        public RiskCentralResponse() {
        }

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
}
