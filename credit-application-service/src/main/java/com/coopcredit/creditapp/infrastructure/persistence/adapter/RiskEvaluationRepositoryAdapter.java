package com.coopcredit.creditapp.infrastructure.persistence.adapter;

import com.coopcredit.creditapp.domain.model.RiskEvaluation;
import com.coopcredit.creditapp.domain.port.RiskEvaluationRepositoryPort;
import com.coopcredit.creditapp.infrastructure.persistence.entity.RiskEvaluationEntity;
import com.coopcredit.creditapp.infrastructure.persistence.mapper.RiskEvaluationEntityMapper;
import com.coopcredit.creditapp.infrastructure.persistence.repository.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementing RiskEvaluationRepositoryPort using JPA.
 */
@Component
public class RiskEvaluationRepositoryAdapter implements RiskEvaluationRepositoryPort {

    private final RiskEvaluationJpaRepository jpaRepository;
    private final RiskEvaluationEntityMapper entityMapper;

    public RiskEvaluationRepositoryAdapter(RiskEvaluationJpaRepository jpaRepository,
            RiskEvaluationEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public RiskEvaluation save(RiskEvaluation evaluation) {
        RiskEvaluationEntity entity = entityMapper.toEntity(evaluation);

        // Set the credit application entity if present
        if (evaluation.getCreditApplication() != null) {
            com.coopcredit.creditapp.infrastructure.persistence.entity.CreditApplicationEntity appEntity = new com.coopcredit.creditapp.infrastructure.persistence.entity.CreditApplicationEntity();
            appEntity.setId(evaluation.getCreditApplication().getId());
            entity.setCreditApplication(appEntity);
        }

        RiskEvaluationEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<RiskEvaluation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId) {
        return jpaRepository.findByCreditApplicationId(creditApplicationId)
                .map(entityMapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
