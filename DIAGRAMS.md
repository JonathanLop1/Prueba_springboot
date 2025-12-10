# 📊 Project Diagrams

## 1. System Architecture

This diagram shows the high-level architecture of the CoopCredit system, including the microservices, database, and external interactions.

```mermaid
graph TD
    User[User / Affiliate]
    Analyst[Credit Analyst]
    
    subgraph "CoopCredit System"
        LB[Load Balancer / Ingress]
        
        subgraph "Credit Application Service"
            API[API REST Controller]
            Auth[Auth & Security]
            UseCase[Business Logic / Use Cases]
            Repo[Repository Adapter]
        end
        
        subgraph "Risk Central Mock"
            RiskAPI[Risk API]
            RiskLogic[Risk Evaluation Logic]
        end
        
        DB[(PostgreSQL Database)]
    end
    
    User -->|HTTP Requests| LB
    Analyst -->|HTTP Requests| LB
    LB --> API
    
    API --> Auth
    Auth --> UseCase
    UseCase --> Repo
    UseCase -->|REST Call| RiskAPI
    
    Repo -->|JDBC| DB
    
    RiskAPI --> RiskLogic
```

## 2. Database Entity-Relationship Diagram (ERD)

This diagram illustrates the data model and relationships between entities in the PostgreSQL database.

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    USERS {
        Long id PK
        String username
        String password
        boolean enabled
    }
    
    USER_ROLES {
        Long user_id FK
        String role
    }
    
    AFFILIATES ||--o{ CREDIT_APPLICATIONS : requests
    AFFILIATES {
        Long id PK
        String document
        String full_name
        BigDecimal salary
        LocalDate affiliation_date
        String status
    }
    
    CREDIT_APPLICATIONS ||--o| RISK_EVALUATIONS : has
    CREDIT_APPLICATIONS {
        Long id PK
        Long affiliate_id FK
        BigDecimal requested_amount
        Integer term_months
        BigDecimal proposed_rate
        String status
        LocalDateTime created_at
    }
    
    RISK_EVALUATIONS {
        Long id PK
        Long application_id FK
        String document
        Integer score
        String risk_level
        String detail
        LocalDateTime evaluation_date
    }
```

## 3. Credit Application Flow (Sequence Diagram)

This sequence diagram details the process of creating and evaluating a credit application.

```mermaid
sequenceDiagram
    actor Affiliate
    actor Analyst
    participant API as Credit Application Service
    participant DB as Database
    participant Risk as Risk Central Mock

    %% Application Creation
    Affiliate->>API: POST /api/applications (Amount, Term)
    activate API
    API->>DB: Get Affiliate Info
    DB-->>API: Affiliate Data
    
    Note over API: Validate Rules:<br/>1. Affiliate Active<br/>2. Seniority > 6 months<br/>3. Amount < 10x Salary<br/>4. Quota/Income < 40%
    
    alt Validation Failed
        API-->>Affiliate: 400 Bad Request (Error Message)
    else Validation Success
        API->>DB: Save Application (PENDING)
        DB-->>API: Saved
        API-->>Affiliate: 201 Created (Application ID)
    end
    deactivate API

    %% Evaluation
    Analyst->>API: POST /api/applications/{id}/evaluate
    activate API
    API->>DB: Get Application
    DB-->>API: Application Data
    
    API->>Risk: POST /risk-evaluation (Document)
    activate Risk
    Risk-->>API: Risk Score & Level
    deactivate Risk
    
    Note over API: Decision Logic:<br/>Score < 500 -> REJECT<br/>Score >= 500 -> APPROVE
    
    API->>DB: Save Risk Evaluation
    API->>DB: Update Application Status
    
    API-->>Analyst: 200 OK (Evaluation Result)
    deactivate API
```

## 4. Class Diagram (Domain Model)

This diagram represents the core domain entities and their relationships within the Hexagonal Architecture.

```mermaid
classDiagram
    class Affiliate {
        -Long id
        -String document
        -String fullName
        -BigDecimal salary
        -AffiliateStatus status
        +canRequestCredit() boolean
        +hasMinimumSeniority(months) boolean
    }

    class CreditApplication {
        -Long id
        -BigDecimal requestedAmount
        -Integer termMonths
        -ApplicationStatus status
        +calculateMonthlyPayment() BigDecimal
        +approve()
        +reject(reason)
    }

    class RiskEvaluation {
        -Long id
        -Integer score
        -RiskLevel level
        -String detail
    }

    class User {
        -Long id
        -String username
        -String password
        -List~UserRole~ roles
    }

    Affiliate "1" -- "*" CreditApplication : makes
    CreditApplication "1" -- "0..1" RiskEvaluation : has
    User "1" -- "*" UserRole : has
```
