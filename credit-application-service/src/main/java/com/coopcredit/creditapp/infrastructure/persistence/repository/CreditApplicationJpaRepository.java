package com.coopcredit.creditapp.infrastructure.persistence.repository;

import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import com.coopcredit.creditapp.infrastructure.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for CreditApplicationEntity.
 */
@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {

    @EntityGraph(attributePaths = { "affiliate", "riskEvaluation" })
    Optional<CreditApplicationEntity> findById(Long id);

    @EntityGraph(attributePaths = { "affiliate" })
    List<CreditApplicationEntity> findByAffiliateId(Long affiliateId);

    @EntityGraph(attributePaths = { "affiliate" })
    List<CreditApplicationEntity> findByStatus(ApplicationStatus status);

    @Query("SELECT ca FROM CreditApplicationEntity ca JOIN FETCH ca.affiliate WHERE ca.id = :id")
    Optional<CreditApplicationEntity> findByIdWithAffiliate(Long id);
}
