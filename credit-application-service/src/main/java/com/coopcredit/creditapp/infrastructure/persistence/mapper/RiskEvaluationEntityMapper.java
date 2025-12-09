package com.coopcredit.creditapp.infrastructure.persistence.mapper;

import com.coopcredit.creditapp.domain.model.RiskEvaluation;
import com.coopcredit.creditapp.infrastructure.persistence.entity.RiskEvaluationEntity;
import org.springframework.stereotype.Component;

/**
 * Simple manual mapper for RiskEvaluationEntity.
 */
@Component
public class RiskEvaluationEntityMapper {

    public RiskEvaluation toDomain(RiskEvaluationEntity entity) {
        if (entity == null) {
            return null;
        }

        RiskEvaluation evaluation = new RiskEvaluation(
                entity.getDocument(),
                entity.getScore(),
                entity.getRiskLevel(),
                entity.getDetail());

        evaluation.setId(entity.getId());
        evaluation.setEvaluationDate(entity.getEvaluationDate());

        return evaluation;
    }

    public RiskEvaluationEntity toEntity(RiskEvaluation evaluation) {
        if (evaluation == null) {
            return null;
        }

        RiskEvaluationEntity entity = new RiskEvaluationEntity();
        entity.setId(evaluation.getId());
        entity.setDocument(evaluation.getDocument());
        entity.setScore(evaluation.getScore());
        entity.setRiskLevel(evaluation.getRiskLevel()); // Direct enum mapping
        entity.setDetail(evaluation.getDetail());
        entity.setEvaluationDate(evaluation.getEvaluationDate());

        return entity;
    }
}
