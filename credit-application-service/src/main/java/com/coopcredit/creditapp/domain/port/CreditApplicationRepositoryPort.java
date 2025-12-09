package com.coopcredit.creditapp.domain.port;

import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import com.coopcredit.creditapp.domain.model.CreditApplication;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for CreditApplication persistence operations.
 * This is implemented by infrastructure adapters.
 */
public interface CreditApplicationRepositoryPort {

    CreditApplication save(CreditApplication application);

    Optional<CreditApplication> findById(Long id);

    List<CreditApplication> findAll();

    List<CreditApplication> findByAffiliateId(Long affiliateId);

    List<CreditApplication> findByStatus(ApplicationStatus status);

    void delete(Long id);
}
