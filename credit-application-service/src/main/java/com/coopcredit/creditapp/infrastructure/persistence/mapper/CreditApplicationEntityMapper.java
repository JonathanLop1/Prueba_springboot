package com.coopcredit.creditapp.infrastructure.persistence.mapper;

import com.coopcredit.creditapp.domain.model.CreditApplication;
import com.coopcredit.creditapp.infrastructure.persistence.entity.CreditApplicationEntity;
import org.springframework.stereotype.Component;

/**
 * Simple manual mapper for CreditApplicationEntity.
 */
@Component
public class CreditApplicationEntityMapper {

    private final AffiliateEntityMapper affiliateMapper;
    private final RiskEvaluationEntityMapper riskEvaluationMapper;

    public CreditApplicationEntityMapper(AffiliateEntityMapper affiliateMapper,
            RiskEvaluationEntityMapper riskEvaluationMapper) {
        this.affiliateMapper = affiliateMapper;
        this.riskEvaluationMapper = riskEvaluationMapper;
    }

    public CreditApplication toDomain(CreditApplicationEntity entity) {
        if (entity == null) {
            return null;
        }

        CreditApplication application = new CreditApplication(
                affiliateMapper.toDomain(entity.getAffiliate()),
                entity.getRequestedAmount(),
                entity.getTermMonths(),
                entity.getProposedRate());
        application.setId(entity.getId());
        application.setApplicationDate(entity.getApplicationDate());
        application.setStatus(entity.getStatus());
        application.setRejectionReason(entity.getRejectionReason());

        // Map risk evaluation if present
        if (entity.getRiskEvaluation() != null) {
            application.setRiskEvaluation(riskEvaluationMapper.toDomain(entity.getRiskEvaluation()));
        }

        return application;
    }

    public CreditApplicationEntity toEntity(CreditApplication application) {
        if (application == null) {
            return null;
        }

        CreditApplicationEntity entity = new CreditApplicationEntity();
        entity.setId(application.getId());
        entity.setAffiliate(affiliateMapper.toEntity(application.getAffiliate()));
        entity.setRequestedAmount(application.getRequestedAmount());
        entity.setTermMonths(application.getTermMonths());
        entity.setProposedRate(application.getProposedRate());
        entity.setApplicationDate(application.getApplicationDate());
        entity.setStatus(application.getStatus());
        entity.setRejectionReason(application.getRejectionReason());

        return entity;
    }
}
