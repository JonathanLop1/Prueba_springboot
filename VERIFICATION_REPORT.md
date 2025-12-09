# 🕵️ Compliance Verification Report

This document details how each requirement of the statement is met and where to find the evidence in the code.

## 1. Functional Requirements

### 1.1 Affiliate Management
> **Requirement**: Register, edit, validate (unique doc, salary > 0, status).
- **Implementation**: `RegisterAffiliateUseCase.java`
- **Validation**:
    - Annotations `@NotNull`, `@Min` in `CreateAffiliateRequest`.
    - Manual Test: `test-api.sh` (Creates affiliate and verifies response).

### 1.2 Application Management
> **Requirement**: Flow PENDING -> Risk Central -> Policies -> APPROVED/REJECTED.
- **Implementation**:
    - `RegisterCreditApplicationUseCase.java`: Creates in PENDING.
    - `EvaluateCreditApplicationUseCase.java`: Calls Risk Central and decides.
- **Unit Tests (`RegisterCreditApplicationUseCaseTest.java`)**:
    - `shouldRegisterApplicationSuccessfully`: Verifies correct creation.
    - `shouldThrowExceptionWhenAmountExceedsLimit`: Verifies 10x salary rule.
    - `shouldThrowExceptionWhenQuotaIncomeRatioExceeded`: Verifies 40% income rule.
    - `shouldThrowExceptionWhenSeniorityIsInsufficient`: Verifies 6 months seniority.

### 1.3 Risk Central Mock
> **Requirement**: POST /risk-evaluation endpoint, deterministic by document.
- **Implementation**: `risk-central-mock-service` (Controller and Service).
- **Test**:
    - The service always returns the same score for the same ID.
    - Verified in `EvaluateCreditApplicationUseCaseTest.java` (response mocks).

### 1.4 Security and Roles
> **Requirement**: JWT, Roles (AFFILIATE, ANALYST, ADMIN).
- **Implementation**: `JwtAuthenticationFilter`, `SecurityConfig`.
- **Integration Tests (`CreditApplicationControllerTest.java`)**:
    - `shouldCreateApplicationSuccessfully`: With `AFFILIATE` role.
    - `shouldEvaluateApplicationSuccessfully`: With `ANALYST` role.
    - `shouldDenyEvaluationAccessForAffiliate`: Verifies that `AFFILIATE` **CANNOT** evaluate (403 Forbidden).

## 2. Non-Functional Requirements

### 2.1 Hexagonal Architecture
> **Requirement**: Pure domains, ports, and adapters.
- **Evidence**: Folder structure:
    - `domain/model`: Pure entities (`Affiliate`, `CreditApplication`).
    - `domain/port`: Interfaces (`RepositoryPort`).
    - `infrastructure/adapter`: Implementations (`JpaAdapter`, `RestController`).

### 2.2 Persistence and Transactions
> **Requirement**: JPA, Relationships, @Transactional.
- **Evidence**:
    - Use of `@Transactional` in UseCases.
    - `@OneToMany` relationship in `AffiliateEntity`.
    - Flyway: Migrations in `src/main/resources/db/migration`.

### 2.3 Observability
> **Requirement**: Actuator, Metrics, Prometheus.
- **Evidence**:
    - `docker-compose.yml`: `prometheus` and `grafana` services.
    - `micrometer-registry-prometheus` dependency in `pom.xml`.
    - Functional `/actuator/prometheus` endpoint.

### 2.4 Testing (The critical completed point)
> **Requirement**: Unit (Mockito) and Integration.
- **Evidence**:
    - `mvn test` runs 13 successful tests.
    - Coverage of success and error cases (business rules).

---

## 🧪 How to Verify It Yourself

### Step 1: Code and Test Verification
Run the automatic tests to validate the internal logic:
```bash
cd credit-application-service
mvn test
```
**Expected result**: `BUILD SUCCESS` with 0 failures.

### Step 2: End-to-End Functional Verification
Run the real integration test script (requires Docker running):
```bash
./test-api.sh
```
**Expected result**:
- Successful Login (Admin, Analyst, Affiliate).
- Affiliate creation.
- Application creation.
- Automatic evaluation (APPROVED/REJECTED).
