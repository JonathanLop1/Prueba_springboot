package com.coopcredit.creditapp.application.mapper;

import com.coopcredit.creditapp.application.dto.RiskEvaluationResponse;
import com.coopcredit.creditapp.domain.model.RiskEvaluation;
import org.springframework.stereotype.Component;

/**
 * Mapper for RiskEvaluation domain to DTO.
 */
@Component
public class RiskEvaluationMapper {

    public RiskEvaluationResponse toResponse(RiskEvaluation evaluation) {
        if (evaluation == null) {
            return null;
        }

        RiskEvaluationResponse response = new RiskEvaluationResponse();
        response.setId(evaluation.getId());
        response.setDocument(evaluation.getDocument());
        response.setScore(evaluation.getScore());
        response.setRiskLevel(evaluation.getRiskLevel().name());
        response.setDetail(evaluation.getDetail());
        response.setEvaluationDate(evaluation.getEvaluationDate());

        return response;
    }
}
