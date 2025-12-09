package com.coopcredit.creditapp.application.mapper;

import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.domain.model.CreditApplication;
import org.springframework.stereotype.Component;

/**
 * Simple manual mapper for CreditApplication.
 */
@Component
public class CreditApplicationMapper {

    private final AffiliateMapper affiliateMapper;
    private final RiskEvaluationMapper riskEvaluationMapper;

    public CreditApplicationMapper(AffiliateMapper affiliateMapper,
            RiskEvaluationMapper riskEvaluationMapper) {
        this.affiliateMapper = affiliateMapper;
        this.riskEvaluationMapper = riskEvaluationMapper;
    }

    public CreditApplicationResponse toResponse(CreditApplication application) {
        if (application == null) {
            return null;
        }

        CreditApplicationResponse response = new CreditApplicationResponse();
        response.setId(application.getId());
        response.setAffiliate(affiliateMapper.toResponse(application.getAffiliate()));
        response.setRequestedAmount(application.getRequestedAmount());
        response.setTermMonths(application.getTermMonths());
        response.setProposedRate(application.getProposedRate());
        response.setApplicationDate(application.getApplicationDate());
        response.setStatus(application.getStatus().name());
        response.setRejectionReason(application.getRejectionReason());

        // Map risk evaluation if present
        if (application.getRiskEvaluation() != null) {
            response.setRiskEvaluation(riskEvaluationMapper.toResponse(application.getRiskEvaluation()));
        }

        return response;
    }
}
