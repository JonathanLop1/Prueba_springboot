package com.coopcredit.creditapp.infrastructure.persistence.repository;

import com.coopcredit.creditapp.infrastructure.persistence.entity.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for RiskEvaluationEntity.
 */
@Repository
public interface RiskEvaluationJpaRepository extends JpaRepository<RiskEvaluationEntity, Long> {

    Optional<RiskEvaluationEntity> findByCreditApplicationId(Long creditApplicationId);
}
