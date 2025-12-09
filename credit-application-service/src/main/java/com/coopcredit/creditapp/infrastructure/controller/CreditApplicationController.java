package com.coopcredit.creditapp.infrastructure.controller;

import com.coopcredit.creditapp.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.usecase.EvaluateCreditApplicationUseCase;
import com.coopcredit.creditapp.application.usecase.RegisterCreditApplicationUseCase;
import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for credit application management endpoints.
 */
@RestController
@RequestMapping("/api/applications")
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class);

    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final CreditApplicationRepositoryPort applicationRepository;
    private final com.coopcredit.creditapp.application.mapper.CreditApplicationMapper applicationMapper;

    public CreditApplicationController(RegisterCreditApplicationUseCase registerCreditApplicationUseCase,
            EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase,
            CreditApplicationRepositoryPort applicationRepository,
            com.coopcredit.creditapp.application.mapper.CreditApplicationMapper applicationMapper) {
        this.registerCreditApplicationUseCase = registerCreditApplicationUseCase;
        this.evaluateCreditApplicationUseCase = evaluateCreditApplicationUseCase;
        this.applicationRepository = applicationRepository;
        this.applicationMapper = applicationMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AFILIADO', 'ADMIN')")
    public ResponseEntity<CreditApplicationResponse> createCreditApplication(
            @Valid @RequestBody CreateCreditApplicationRequest request) {
        log.info("Creating credit application for affiliate ID: {}", request.getAffiliateId());
        CreditApplicationResponse response = registerCreditApplicationUseCase.execute(request);
        log.info("Credit application created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    public ResponseEntity<CreditApplicationResponse> evaluateCreditApplication(@PathVariable Long id) {
        log.info("Evaluating credit application with ID: {}", id);
        CreditApplicationResponse response = evaluateCreditApplicationUseCase.execute(id);
        log.info("Credit application evaluated successfully with status: {}", response.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreditApplicationResponse> getCreditApplication(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Fetching credit application with ID: {}", id);
        return applicationRepository.findById(id)
                .map(applicationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CreditApplicationResponse>> getCreditApplications(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        log.info("Fetching credit applications, status filter: {}", status);

        List<CreditApplicationResponse> applications;

        if (status != null) {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applications = applicationRepository.findByStatus(appStatus).stream()
                    .map(applicationMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            applications = applicationRepository.findAll().stream()
                    .map(applicationMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(applications);
    }
}
