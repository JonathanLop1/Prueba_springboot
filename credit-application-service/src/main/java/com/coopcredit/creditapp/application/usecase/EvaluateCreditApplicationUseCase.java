package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.mapper.CreditApplicationMapper;
import com.coopcredit.creditapp.domain.model.CreditApplication;
import com.coopcredit.creditapp.domain.model.RiskEvaluation;
import com.coopcredit.creditapp.domain.model.RiskLevel;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import com.coopcredit.creditapp.domain.port.RiskCentralPort;
import com.coopcredit.creditapp.domain.port.RiskEvaluationRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for evaluating a credit application.
 * This orchestrates the entire evaluation process including risk assessment.
 */
@Service
public class EvaluateCreditApplicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(EvaluateCreditApplicationUseCase.class);

    private static final int HIGH_RISK_THRESHOLD = 500;

    private final CreditApplicationRepositoryPort applicationRepository;
    private final RiskEvaluationRepositoryPort riskEvaluationRepository;
    private final RiskCentralPort riskCentralPort;
    private final CreditApplicationMapper applicationMapper;

    public EvaluateCreditApplicationUseCase(CreditApplicationRepositoryPort applicationRepository,
            RiskEvaluationRepositoryPort riskEvaluationRepository,
            RiskCentralPort riskCentralPort,
            CreditApplicationMapper applicationMapper) {
        this.applicationRepository = applicationRepository;
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.riskCentralPort = riskCentralPort;
        this.applicationMapper = applicationMapper;
    }

    @Transactional
    public CreditApplicationResponse execute(Long applicationId) {
        log.info("Starting evaluation for credit application: {}", applicationId);

        // Find application
        CreditApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found: " + applicationId));

        // Business rule: only evaluate PENDING applications
        if (!application.isPending()) {
            throw new IllegalStateException("Only PENDING applications can be evaluated");
        }

        // Call risk central service
        log.info("Calling risk central for document: {}", application.getAffiliate().getDocument());
        RiskCentralPort.RiskEvaluationResponse riskResponse = riskCentralPort.evaluateRisk(
                application.getAffiliate().getDocument(),
                application.getRequestedAmount(),
                application.getTermMonths());

        // Create risk evaluation entity
        RiskLevel riskLevel = RiskLevel.valueOf(riskResponse.getRiskLevel());
        RiskEvaluation riskEvaluation = new RiskEvaluation(
                riskResponse.getDocument(),
                riskResponse.getScore(),
                riskLevel,
                riskResponse.getDetail());
        riskEvaluation.setCreditApplication(application);

        // Save risk evaluation
        RiskEvaluation savedEvaluation = riskEvaluationRepository.save(riskEvaluation);
        application.setRiskEvaluation(savedEvaluation);

        // Apply decision logic
        if (riskResponse.getScore() < HIGH_RISK_THRESHOLD) {
            String reason = String.format(
                    "Rejected due to high risk. Score: %d, Level: %s",
                    riskResponse.getScore(),
                    riskResponse.getRiskLevel());
            application.reject(reason);
            log.info("Application {} REJECTED: {}", applicationId, reason);
        } else {
            application.approve();
            log.info("Application {} APPROVED", applicationId);
        }

        // Save updated application
        CreditApplication updated = applicationRepository.save(application);

        log.info("Evaluation completed for application: {}", applicationId);
        return applicationMapper.toResponse(updated);
    }
}
