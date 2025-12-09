package com.coopcredit.creditapp.infrastructure.persistence.entity;

import com.coopcredit.creditapp.domain.model.AffiliateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for Affiliate persistence.
 * This is the infrastructure adapter for the domain Affiliate.
 */
@Entity
@Table(name = "affiliates", indexes = {
        @Index(name = "idx_affiliate_document", columnList = "document", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String document;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(nullable = false)
    private LocalDate affiliationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AffiliateStatus status;

    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditApplicationEntity> creditApplications = new ArrayList<>();
}
