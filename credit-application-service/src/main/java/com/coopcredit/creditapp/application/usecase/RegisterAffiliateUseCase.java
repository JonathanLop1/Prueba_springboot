package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.AffiliateResponse;
import com.coopcredit.creditapp.application.dto.CreateAffiliateRequest;
import com.coopcredit.creditapp.application.mapper.AffiliateMapper;
import com.coopcredit.creditapp.domain.model.Affiliate;
import com.coopcredit.creditapp.domain.port.AffiliateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for registering a new affiliate.
 * This encapsulates all business logic for affiliate registration.
 */
@Service
public class RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;
    private final AffiliateMapper affiliateMapper;

    public RegisterAffiliateUseCase(AffiliateRepositoryPort affiliateRepository,
            AffiliateMapper affiliateMapper) {
        this.affiliateRepository = affiliateRepository;
        this.affiliateMapper = affiliateMapper;
    }

    @Transactional
    public AffiliateResponse execute(CreateAffiliateRequest request) {
        // Business rule: document must be unique
        if (affiliateRepository.existsByDocument(request.getDocument())) {
            throw new IllegalArgumentException("Document already exists: " + request.getDocument());
        }

        // Create domain entity
        Affiliate affiliate = new Affiliate(
                request.getDocument(),
                request.getFullName(),
                request.getSalary(),
                request.getAffiliationDate());

        // Save
        Affiliate saved = affiliateRepository.save(affiliate);

        // Return DTO
        return affiliateMapper.toResponse(saved);
    }
}
