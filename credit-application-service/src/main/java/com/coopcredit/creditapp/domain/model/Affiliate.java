package com.coopcredit.creditapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing an affiliate in the CoopCredit system.
 * This is a pure domain object with no framework dependencies.
 */
public class Affiliate {
    
    private Long id;
    private String document;
    private String fullName;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;
    private List<CreditApplication> creditApplications;
    
    public Affiliate() {
        this.creditApplications = new ArrayList<>();
        this.status = AffiliateStatus.ACTIVE;
    }
    
    public Affiliate(String document, String fullName, BigDecimal salary, LocalDate affiliationDate) {
        this();
        this.document = document;
        this.fullName = fullName;
        this.salary = salary;
        this.affiliationDate = affiliationDate;
    }
    
    // Business logic methods
    
    public boolean canRequestCredit() {
        return this.status == AffiliateStatus.ACTIVE;
    }
    
    public boolean hasMinimumSeniority(int monthsRequired) {
        if (affiliationDate == null) {
            return false;
        }
        LocalDate requiredDate = LocalDate.now().minusMonths(monthsRequired);
        return affiliationDate.isBefore(requiredDate) || affiliationDate.isEqual(requiredDate);
    }
    
    public void activate() {
        this.status = AffiliateStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.status = AffiliateStatus.INACTIVE;
    }
    
    public void addCreditApplication(CreditApplication application) {
        if (this.creditApplications == null) {
            this.creditApplications = new ArrayList<>();
        }
        this.creditApplications.add(application);
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDocument() {
        return document;
    }
    
    public void setDocument(String document) {
        this.document = document;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public LocalDate getAffiliationDate() {
        return affiliationDate;
    }
    
    public void setAffiliationDate(LocalDate affiliationDate) {
        this.affiliationDate = affiliationDate;
    }
    
    public AffiliateStatus getStatus() {
        return status;
    }
    
    public void setStatus(AffiliateStatus status) {
        this.status = status;
    }
    
    public List<CreditApplication> getCreditApplications() {
        return creditApplications;
    }
    
    public void setCreditApplications(List<CreditApplication> creditApplications) {
        this.creditApplications = creditApplications;
    }
}
