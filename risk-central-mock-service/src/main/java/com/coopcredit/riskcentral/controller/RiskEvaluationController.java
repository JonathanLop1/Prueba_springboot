package com.coopcredit.riskcentral.controller;

import com.coopcredit.riskcentral.dto.RiskEvaluationRequest;
import com.coopcredit.riskcentral.dto.RiskEvaluationResponse;
import com.coopcredit.riskcentral.service.RiskEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for risk evaluation endpoints.
 */
@RestController
@RequestMapping
public class RiskEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationController.class);

    private final RiskEvaluationService riskEvaluationService;

    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }

    @PostMapping("/risk-evaluation")
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(@Valid @RequestBody RiskEvaluationRequest request) {
        log.info("Risk evaluation requested for document: {}", request.getDocumento());

        RiskEvaluationResponse response = riskEvaluationService.evaluateRisk(request);

        log.info("Risk evaluation completed: score={}, level={}", response.getScore(), response.getNivelRiesgo());

        return ResponseEntity.ok(response);
    }
}
