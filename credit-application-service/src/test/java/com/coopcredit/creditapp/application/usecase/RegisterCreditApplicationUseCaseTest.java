package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.mapper.CreditApplicationMapper;
import com.coopcredit.creditapp.domain.model.Affiliate;
import com.coopcredit.creditapp.domain.model.AffiliateStatus;
import com.coopcredit.creditapp.domain.model.CreditApplication;
import com.coopcredit.creditapp.domain.port.AffiliateRepositoryPort;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCreditApplicationUseCaseTest {

    @Mock
    private CreditApplicationRepositoryPort applicationRepository;

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    @Mock
    private CreditApplicationMapper applicationMapper;

    @InjectMocks
    private RegisterCreditApplicationUseCase useCase;

    private Affiliate activeAffiliate;
    private CreateCreditApplicationRequest validRequest;

    @BeforeEach
    void setUp() {
        activeAffiliate = new Affiliate(
                "1234567890",
                "John Doe",
                new BigDecimal("5000000"), // 5M salary
                LocalDate.now().minusMonths(12) // 1 year seniority
        );
        activeAffiliate.setId(1L);
        activeAffiliate.setStatus(AffiliateStatus.ACTIVE);

        validRequest = new CreateCreditApplicationRequest();
        validRequest.setAffiliateId(1L);
        validRequest.setRequestedAmount(new BigDecimal("10000000")); // 10M
        validRequest.setTermMonths(24);
        validRequest.setProposedRate(new BigDecimal("1.5"));
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {
        // Arrange
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(activeAffiliate));
        when(applicationRepository.save(any(CreditApplication.class))).thenAnswer(i -> i.getArguments()[0]);
        when(applicationMapper.toResponse(any(CreditApplication.class))).thenReturn(new CreditApplicationResponse());

        // Act
        CreditApplicationResponse response = useCase.execute(validRequest);

        // Assert
        assertNotNull(response);
        verify(affiliateRepository).findById(1L);
        verify(applicationRepository).save(any(CreditApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenAffiliateNotFound() {
        // Arrange
        when(affiliateRepository.findById(99L)).thenReturn(Optional.empty());
        validRequest.setAffiliateId(99L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(validRequest));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAffiliateInactive() {
        // Arrange
        activeAffiliate.setStatus(AffiliateStatus.INACTIVE);
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(activeAffiliate));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.execute(validRequest));
        assertEquals("Affiliate must be ACTIVE to request credit", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSeniorityIsInsufficient() {
        // Arrange
        activeAffiliate.setAffiliationDate(LocalDate.now().minusMonths(1)); // Only 1 month
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(activeAffiliate));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.execute(validRequest));
        assertTrue(exception.getMessage().contains("must have at least 6 months"));
    }

    @Test
    void shouldThrowExceptionWhenAmountExceedsLimit() {
        // Arrange
        // Salary 5M * 10 = 50M max. Request 60M.
        validRequest.setRequestedAmount(new BigDecimal("60000000"));
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(activeAffiliate));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(validRequest));
        assertTrue(exception.getMessage().contains("exceeds maximum allowed"));
    }

    @Test
    void shouldThrowExceptionWhenQuotaIncomeRatioExceeded() {
        // Arrange
        // Salary 5M. Max quota 40% = 2M.
        // Request huge amount/short term to exceed quota
        validRequest.setRequestedAmount(new BigDecimal("40000000"));
        validRequest.setTermMonths(12); // High monthly payment
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(activeAffiliate));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(validRequest));
        assertTrue(exception.getMessage().contains("exceeds maximum quota/income ratio"));
    }
}
