package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.CreditApplicationResponse;
import com.coopcredit.creditapp.application.mapper.CreditApplicationMapper;
import com.coopcredit.creditapp.domain.model.*;
import com.coopcredit.creditapp.domain.port.CreditApplicationRepositoryPort;
import com.coopcredit.creditapp.domain.port.RiskCentralPort;
import com.coopcredit.creditapp.domain.port.RiskEvaluationRepositoryPort;
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
class EvaluateCreditApplicationUseCaseTest {

    @Mock
    private CreditApplicationRepositoryPort applicationRepository;

    @Mock
    private RiskEvaluationRepositoryPort riskEvaluationRepository;

    @Mock
    private RiskCentralPort riskCentralPort;

    @Mock
    private CreditApplicationMapper applicationMapper;

    @InjectMocks
    private EvaluateCreditApplicationUseCase useCase;

    private CreditApplication pendingApplication;
    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        affiliate = new Affiliate(
                "1234567890",
                "Jane Doe",
                new BigDecimal("5000000"),
                LocalDate.now().minusYears(2));
        affiliate.setId(1L);
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        pendingApplication = new CreditApplication(
                affiliate,
                new BigDecimal("10000000"),
                24,
                new BigDecimal("1.5"));// Ensure ID is set if needed, or rely on object identity
    }

    @Test
    void shouldApproveApplicationWhenScoreIsHigh() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApplication));

        RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse();
        riskResponse.setDocument("1234567890");
        riskResponse.setScore(700); // High score
        riskResponse.setRiskLevel("LOW");
        riskResponse.setDetail("Good history");

        when(riskCentralPort.evaluateRisk(any(), any(), anyInt())).thenReturn(riskResponse);
        when(riskEvaluationRepository.save(any(RiskEvaluation.class))).thenAnswer(i -> i.getArguments()[0]);
        when(applicationRepository.save(any(CreditApplication.class))).thenAnswer(i -> i.getArguments()[0]);
        when(applicationMapper.toResponse(any(CreditApplication.class))).thenReturn(new CreditApplicationResponse());

        // Act
        CreditApplicationResponse response = useCase.execute(1L);

        // Assert
        assertEquals(ApplicationStatus.APPROVED, pendingApplication.getStatus());
        verify(riskCentralPort).evaluateRisk(eq("1234567890"), any(), anyInt());
        verify(applicationRepository).save(pendingApplication);
    }

    @Test
    void shouldRejectApplicationWhenScoreIsLow() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApplication));

        RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse();
        riskResponse.setDocument("1234567890");
        riskResponse.setScore(400); // Low score (< 500)
        riskResponse.setRiskLevel("HIGH");
        riskResponse.setDetail("Bad history");

        when(riskCentralPort.evaluateRisk(any(), any(), anyInt())).thenReturn(riskResponse);
        when(riskEvaluationRepository.save(any(RiskEvaluation.class))).thenAnswer(i -> i.getArguments()[0]);
        when(applicationRepository.save(any(CreditApplication.class))).thenAnswer(i -> i.getArguments()[0]);
        when(applicationMapper.toResponse(any(CreditApplication.class))).thenReturn(new CreditApplicationResponse());

        // Act
        useCase.execute(1L);

        // Assert
        assertEquals(ApplicationStatus.REJECTED, pendingApplication.getStatus());
        assertNotNull(pendingApplication.getRejectionReason());
        verify(applicationRepository).save(pendingApplication);
    }

    @Test
    void shouldThrowExceptionWhenApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
    }

    @Test
    void shouldThrowExceptionWhenApplicationNotPending() {
        // Arrange
        pendingApplication.approve(); // Already approved
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApplication));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> useCase.execute(1L));
        assertEquals("Only PENDING applications can be evaluated", exception.getMessage());
    }
}
