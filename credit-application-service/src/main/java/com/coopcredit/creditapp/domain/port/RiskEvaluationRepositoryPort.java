package com.coopcredit.creditapp.domain.port;

import com.coopcredit.creditapp.domain.model.RiskEvaluation;

import java.util.Optional;

/**
 * Port interface for RiskEvaluation persistence operations.
 * This is implemented by infrastructure adapters.
 */
public interface RiskEvaluationRepositoryPort {

    RiskEvaluation save(RiskEvaluation evaluation);

    Optional<RiskEvaluation> findById(Long id);

    Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId);

    void delete(Long id);
}
