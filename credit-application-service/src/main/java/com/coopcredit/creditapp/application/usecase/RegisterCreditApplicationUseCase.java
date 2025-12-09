package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.mapper.CreditApplicationMapper;
import com.coopcredit.creditapp.domain.model.Affiliate;
import com.coopcredit.creditapp.domain.model.CreditApplication;
import com.coopcredit.creditapp.domain.port.AffiliateRepositoryPort;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Use case for registering a new credit application.
 * This encapsulates all business logic for credit application creation.
 */
@Service
public class RegisterCreditApplicationUseCase {

    private static final int MINIMUM_SENIORITY_MONTHS = 6;
    private static final BigDecimal MAX_CREDIT_MULTIPLIER = BigDecimal.valueOf(10); // 10x salary
    private static final BigDecimal MAX_QUOTA_INCOME_RATIO = BigDecimal.valueOf(0.40); // 40%

    private final CreditApplicationRepositoryPort applicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final CreditApplicationMapper applicationMapper;

    public RegisterCreditApplicationUseCase(CreditApplicationRepositoryPort applicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            CreditApplicationMapper applicationMapper) {
        this.applicationRepository = applicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.applicationMapper = applicationMapper;
    }

    @Transactional
    public CreditApplicationResponse execute(CreateCreditApplicationRequest request) {
        // Find affiliate
        Affiliate affiliate = affiliateRepository.findById(request.getAffiliateId())
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found: " + request.getAffiliateId()));

        // Business rule: affiliate must be ACTIVE
        if (!affiliate.canRequestCredit()) {
            throw new IllegalStateException("Affiliate must be ACTIVE to request credit");
        }

        // Business rule: minimum seniority
        if (!affiliate.hasMinimumSeniority(MINIMUM_SENIORITY_MONTHS)) {
            throw new IllegalStateException(
                    String.format("Affiliate must have at least %d months of seniority", MINIMUM_SENIORITY_MONTHS));
        }

        // Business rule: maximum amount based on salary
        BigDecimal maxAmount = affiliate.getSalary().multiply(MAX_CREDIT_MULTIPLIER);
        if (request.getRequestedAmount().compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException(
                    String.format("Requested amount exceeds maximum allowed (%.2f)", maxAmount));
        }

        // Business rule: quota/income ratio
        CreditApplication tempApplication = new CreditApplication(
                affiliate,
                request.getRequestedAmount(),
                request.getTermMonths(),
                request.getProposedRate());
        BigDecimal monthlyPayment = tempApplication.calculateMonthlyPayment();
        BigDecimal quotaIncomeRatio = monthlyPayment.divide(affiliate.getSalary(), 4, BigDecimal.ROUND_HALF_UP);

        if (quotaIncomeRatio.compareTo(MAX_QUOTA_INCOME_RATIO) > 0) {
            throw new IllegalArgumentException(
                    String.format("Monthly payment (%.2f) exceeds maximum quota/income ratio (%.0f%%)",
                            monthlyPayment, MAX_QUOTA_INCOME_RATIO.multiply(BigDecimal.valueOf(100))));
        }

        // Create and save
        CreditApplication application = new CreditApplication(
                affiliate,
                request.getRequestedAmount(),
                request.getTermMonths(),
                request.getProposedRate());

        CreditApplication saved = applicationRepository.save(application);

        return applicationMapper.toResponse(saved);
    }
}
