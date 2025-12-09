package com.coopcredit.creditapp.domain.port;

import com.coopcredit.creditapp.domain.model.Affiliate;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Affiliate persistence operations.
 * This is implemented by infrastructure adapters.
 */
public interface AffiliateRepositoryPort {

    Affiliate save(Affiliate affiliate);

    Optional<Affiliate> findById(Long id);

    Optional<Affiliate> findByDocument(String document);

    List<Affiliate> findAll();

    boolean existsByDocument(String document);

    void delete(Long id);
}
