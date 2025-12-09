package com.coopcredit.creditapp.infrastructure.controller;

import com.coopcredit.creditapp.application.dto.AffiliateResponse;
import com.coopcredit.creditapp.application.dto.CreateAffiliateRequest;
import com.coopcredit.creditapp.application.usecase.RegisterAffiliateUseCase;
import com.coopcredit.creditapp.domain.port.AffiliateRepositoryPort;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for affiliate management endpoints.
 */
@RestController
@RequestMapping("/api/affiliates")
public class AffiliateController {

    private static final Logger log = LoggerFactory.getLogger(AffiliateController.class);

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final AffiliateRepositoryPort affiliateRepository;
    private final com.coopcredit.creditapp.application.mapper.AffiliateMapper affiliateMapper;

    public AffiliateController(RegisterAffiliateUseCase registerAffiliateUseCase,
            AffiliateRepositoryPort affiliateRepository,
            com.coopcredit.creditapp.application.mapper.AffiliateMapper affiliateMapper) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.affiliateRepository = affiliateRepository;
        this.affiliateMapper = affiliateMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    public ResponseEntity<AffiliateResponse> createAffiliate(@Valid @RequestBody CreateAffiliateRequest request) {
        log.info("Creating affiliate with document: {}", request.getDocument());
        AffiliateResponse response = registerAffiliateUseCase.execute(request);
        log.info("Affiliate created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AffiliateResponse> getAffiliate(@PathVariable Long id) {
        log.info("Fetching affiliate with ID: {}", id);
        return affiliateRepository.findById(id)
                .map(affiliateMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AffiliateResponse>> getAllAffiliates() {
        log.info("Fetching all affiliates");
        List<AffiliateResponse> affiliates = affiliateRepository.findAll().stream()
                .map(affiliateMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(affiliates);
    }
}
