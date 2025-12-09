package com.coopcredit.creditapp.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new affiliate.
 */
public class CreateAffiliateRequest {

    @NotBlank(message = "Document is required")
    @Pattern(regexp = "^[0-9]{6,15}$", message = "Document must be between 6 and 15 digits")
    private String document;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 200, message = "Full name must be between 3 and 200 characters")
    private String fullName;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "Affiliation date is required")
    @PastOrPresent(message = "Affiliation date cannot be in the future")
    private LocalDate affiliationDate;

    public CreateAffiliateRequest() {
    }

    public CreateAffiliateRequest(String document, String fullName, BigDecimal salary, LocalDate affiliationDate) {
        this.document = document;
        this.fullName = fullName;
        this.salary = salary;
        this.affiliationDate = affiliationDate;
    }

    // Getters and Setters

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
}
