package com.coopcredit.creditapp.infrastructure.persistence.entity;

import com.coopcredit.creditapp.domain.model.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for CreditApplication persistence.
 * This is the infrastructure adapter for the domain CreditApplication.
 */
@Entity
@Table(name = "credit_applications", indexes = {
        @Index(name = "idx_application_status", columnList = "status"),
        @Index(name = "idx_application_affiliate", columnList = "affiliate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal proposedRate;

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskEvaluationEntity riskEvaluation;

    @Column(length = 500)
    private String rejectionReason;
}
