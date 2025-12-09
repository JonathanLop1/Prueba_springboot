# Credit Management System - CoopCredit

## 📋 Description

Credit application management system based on microservices with hexagonal architecture. It allows the management of affiliates, credit applications, and credit risk evaluation through integration with an external mock service.

### Key Features

- ✅ JWT Authentication with roles (ADMIN, ANALYST, AFFILIATE)
- ✅ Complete affiliate management (CRUD)
- ✅ Credit applications with full flow PENDING → APPROVED/REJECTED
- ✅ Automatic risk evaluation via external microservice
- ✅ Hexagonal Architecture (Ports & Adapters)
- ✅ Persistence with JPA and PostgreSQL
- ✅ Automatic migrations with Flyway
- ✅ Observability with Spring Boot Actuator + Micrometer
- ✅ Error handling with RFC 7807 (ProblemDetail)
- ✅ Docker Compose for full deployment

---

## 🏗️ Architecture

### Microservices

1. **credit-application-service** (Port 8080)
   - Affiliate management
   - Credit application management
   - Authentication and authorization
   - Application evaluation

2. **risk-central-mock-service** (Port 8081)
   - Credit risk evaluation
   - Deterministic algorithm based on document hash

3. **PostgreSQL Database** (Port 5432)
   - Main database
   - Migrations with Flyway

### Hexagonal Architecture

```
┌─────────────────────────────────────┐
│         API REST Layer              │
│     (Controllers)                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Application Layer              │
│  (Use Cases, DTOs, Mappers)         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Domain Layer                │
│  (Entities, Ports, Business Logic)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    Infrastructure Layer             │
│  (Adapters: JPA, REST, Security)    │
└─────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17 (only for local development)
- Maven 3.8+ (only for local development)

### Execution with Docker Compose (Recommended)

```bash
# Clone the repository
cd /path/to/project

# Start all services
docker-compose up --build -d

# Verify status
docker-compose ps

# View logs
docker-compose logs -f credit-application
```

Services will be available at:
- **Main API**: http://localhost:8080
- **Risk Central**: http://localhost:8081  
- **PostgreSQL**: localhost:5432

### Local Execution (Development)

```bash
# 1. Start PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=coopcredit_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine

# 2. Start Risk Central Mock
cd risk-central-mock-service
mvn spring-boot:run

# 3. Start Credit Application Service
cd credit-application-service
mvn spring-boot:run
```

---

## 🔐 Authentication

### Predefined Users

All users have the password: `Admin123`

| User | Password | Role | Description |
|---------|----------|-----|-------------|
| `admin` | `Admin123` | ROLE_ADMIN | Full system access |
| `analyst1` | `Admin123` | ROLE_ANALISTA | Evaluate applications |
| `affiliate1` | `Admin123` | ROLE_AFILIADO | Create applications |

### Login Example

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "admin"
}
```

**Using the Token:**
```bash
curl -H "Authorization: Bearer {token}" http://localhost:8080/api/affiliates
```

---

## 📚 API Endpoints

### Base URL
```
http://localhost:8080
```

### Authentication (`/api/auth`)

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login | No |

### Affiliates (`/api/affiliates`)

| Method | Endpoint | Description | Allowed Roles |
|--------|----------|-------------|------------------|
| POST | `/api/affiliates` | Create affiliate | ADMIN, ANALYST |
| GET | `/api/affiliates` | List affiliates | Authenticated |
| GET | `/api/affiliates/{id}` | View affiliate | Authenticated |
| PUT | `/api/affiliates/{id}` | Update affiliate | ADMIN |
| DELETE | `/api/affiliates/{id}` | Delete affiliate | ADMIN |

### Credit Applications (`/api/applications`)

| Method | Endpoint | Description | Allowed Roles |
|--------|----------|-------------|------------------|
| POST | `/api/applications` | Create application | AFFILIATE, ADMIN |
| GET | `/api/applications` | List applications | Authenticated |
| GET | `/api/applications/{id}` | View application | Authenticated |
| POST | `/api/applications/{id}/evaluate` | Evaluate application | ANALYST, ADMIN |

### Monitoring (`/actuator`)

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | System status |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus format metrics |
| `/actuator/info` | Application information |

---

## 💡 Usage Examples

### 1. Create an Affiliate

```bash
TOKEN="<admin-token>"

curl -X POST http://localhost:8080/api/affiliates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "document": "1234567890",
    "fullName": "John Doe",
    "salary": 5000000,
    "affiliationDate": "2023-01-15"
  }'
```

### 2. Create Credit Application

```bash
AFFILIATE_TOKEN="<affiliate-token>"

curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AFFILIATE_TOKEN" \
  -d '{
    "affiliateId": 1,
    "requestedAmount": 10000000,
    "termMonths": 24,
    "proposedRate": 1.5
  }'
```

### 3. Evaluate Application

```bash
ANALYST_TOKEN="<analyst-token>"

curl -X POST http://localhost:8080/api/applications/1/evaluate \
  -H "Authorization: Bearer $ANALYST_TOKEN"
```

---

## 🔄 Credit Evaluation Flow

1. **Affiliate creates application** → Status: PENDING
2. **System calls Risk Central** → Gets score and risk level
3. **Applies internal policies:**
   - Score < 500 → REJECTED
   - Score >= 500 → APPROVED
4. **Updates application** with result and evaluation

---

## 🧪 Testing

### 1. Unit and Integration Tests (JUnit 5 + Mockito)
The project has a complete suite of automatic tests covering:
- **Use Cases**: `RegisterCreditApplicationUseCase`, `EvaluateCreditApplicationUseCase`.
- **Controllers**: `CreditApplicationController` (with MockMvc).
- **Business Rules**: Validations of amounts, seniority, debt capacity.

To run the tests:
```bash
cd credit-application-service
mvn test
```

### 2. End-to-End Test Script
```bash
./test-api.sh
```
This script tests the complete flow in a deployed environment:
- ✅ Authentication (Admin, Analyst, Affiliate)
- ✅ Affiliate creation
- ✅ Affiliate listing
- ✅ Application creation
- ✅ Application evaluation
- ✅ Health checks

### 3. Verification Report
For a complete detail of how each project requirement is met, consult the file:
📄 [VERIFICATION_REPORT.md](VERIFICATION_REPORT.md)

---

## ☁️ Deployment on Render.com

The project includes configuration ready to deploy on Render.com using Blueprints.

### Deployment Files
- `render.yaml`: Infrastructure as Code Blueprint.
- `DEPLOY_RENDER.md`: Step-by-step guide for deployment.

To deploy, follow the detailed instructions in:
📄 [Deployment Guide on Render](DEPLOY_RENDER.md)

---

## 🛠️ Technologies

### Credit Application Service
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 16
- Flyway
- Lombok
- Micrometer

### Risk Central Mock Service
- Java 17
- Spring Boot 3.2.0
- Spring Web

### DevOps
- Docker & Docker Compose
- Maven
- Git

---

## 📊 Data Model

### Affiliate
```json
{
  "id": 1,
  "document": "1234567890",
  "fullName": "John Doe",
  "salary": 5000000.00,
  "affiliationDate": "2023-01-15",
  "status": "ACTIVE"
}
```

### Credit Application
```json
{
  "id": 1,
  "affiliate": { ... },
  "requestedAmount": 10000000.00,
  "termMonths": 24,
  "proposedRate": 1.50,
  "applicationDate": "2025-12-09T19:21:31.174",
  "status": "APPROVED",
  "riskEvaluation": { ... },
  "rejectionReason": null
}
```

### Risk Evaluation
```json
{
  "id": 1,
  "document": "1234567890",
  "score": 642,
  "riskLevel": "MEDIUM",
  "detail": "Moderate credit history",
  "evaluationDate": "2025-12-09T19:21:31.345"
}
```

---

## 🐳 Docker

### Useful Commands

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Clean all (includes volumes)
docker-compose down -v

# Rebuild images
docker-compose up --build -d

# View status
docker-compose ps
```

### Connection to PostgreSQL

```bash
docker exec -it coopcredit-db psql -U postgres -d coopcredit_db
```

---

## 📈 Metrics and Observability

### Verify System Status

```bash
curl http://localhost:8080/actuator/health | jq .
```

### View Metrics

```bash
# General metrics
curl http://localhost:8080/actuator/metrics | jq '.names'

# Specific metrics
curl http://localhost:8080/actuator/metrics/http.server.requests | jq .
```

### Custom Metrics Implemented

- `credit_applications_created_total` - Total applications created
- `credit_applications_evaluated_total` - Total applications evaluated
- `http_request_duration_seconds` - HTTP request duration

---

## 🔧 Configuration

### Environment Variables (application.yml)

```yaml
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/coopcredit_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Risk Central
risk.central.url=http://localhost:8081
```

---

## 🤝 Developers

- Architecture: Hexagonal (Ports & Adapters)
- Patterns: Repository, Use Case, DTO, Builder
- Principles: SOLID, Clean Code, DRY

---

## 📄 License

This project is an educational example for microservices architecture demonstration.

---

## 📞 Support

To report issues or suggestions, please open a ticket in the repository.
