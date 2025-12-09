# Sistema de Gestión de Créditos - CoopCredit

## 📋 Descripción

Sistema de gestión de solicitudes de crédito basado en microservicios con arquitectura hexagonal. Permite la gestión de afiliados, solicitudes de crédito y evaluación de riesgo crediticio mediante integración con un servicio externo mock.

### Características Principales

- ✅ Autenticación JWT con roles (ADMIN, ANALISTA, AFILIADO)
- ✅ Gestión completa de afiliados (CRUD)
- ✅ Solicitudes de crédito con flujo completo PENDIENTE → APROBADO/RECHAZADO
- ✅ Evaluación automática de riesgo mediante microservicio externo
- ✅ Arquitectura Hexagonal (Ports & Adapters)
- ✅ Persistencia con JPA y PostgreSQL
- ✅ Migraciones automáticas con Flyway
- ✅ Observabilidad con Spring Boot Actuator + Micrometer
- ✅ Manejo de errores con RFC 7807 (ProblemDetail)
- ✅ Docker Compose para despliegue completo

---

## 🏗️ Arquitectura

### Microservicios

1. **credit-application-service** (Puerto 8080)
   - Gestión de afiliados
   - Gestión de solicitudes de crédito
   - Autenticación y autorización
   - Evaluación de solicitudes

2. **risk-central-mock-service** (Puerto 8081)
   - Evaluación de riesgo crediticio
   - Algoritmo determinista basado en hash del documento

3. **PostgreSQL Database** (Puerto 5432)
   - Base de datos principal
   - Migraciones con Flyway

### Arquitectura Hexagonal

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

## 🚀 Inicio Rápido

### Requisitos Previos

- Docker & Docker Compose
- Java 17 (solo para desarrollo local)
- Maven 3.8+ (solo para desarrollo local)

### Ejecución con Docker Compose (Recomendado)

```bash
# Clonar el repositorio
cd /ruta/al/proyecto

# Iniciar todos los servicios
docker-compose up --build -d

# Verificar estado
docker-compose ps

# Ver logs
docker-compose logs -f credit-application
```

Los servicios estarán disponibles en:
- **API Principal**: http://localhost:8080
- **Risk Central**: http://localhost:8081  
- **PostgreSQL**: localhost:5432

### Ejecución Local (Desarrollo)

```bash
# 1. Iniciar PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=coopcredit_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine

# 2. Iniciar Risk Central Mock
cd risk-central-mock-service
mvn spring-boot:run

# 3. Iniciar Credit Application Service
cd credit-application-service
mvn spring-boot:run
```

---

## 🔐 Autenticación

### Usuarios Predefinidos

Todos los usuarios tienen la contraseña: `Admin123`

| Usuario | Password | Rol | Descripción |
|---------|----------|-----|-------------|
| `admin` | `Admin123` | ROLE_ADMIN | Acceso total al sistema |
| `analyst1` | `Admin123` | ROLE_ANALISTA | Evaluar solicitudes |
| `affiliate1` | `Admin123` | ROLE_AFILIADO | Crear solicitudes |

### Ejemplo de Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin123"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "admin"
}
```

**Uso del Token:**
```bash
curl -H "Authorization: Bearer {token}" http://localhost:8080/api/affiliates
```

---

## 📚 API Endpoints

### Base URL
```
http://localhost:8080
```

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Registrar nuevo usuario | No |
| POST | `/api/auth/login` | Iniciar sesión | No |

### Afiliados (`/api/affiliates`)

| Método | Endpoint | Descripción | Roles Permitidos |
|--------|----------|-------------|------------------|
| POST | `/api/affiliates` | Crear afiliado | ADMIN, ANALISTA |
| GET | `/api/affiliates` | Listar afiliados | Autenticado |
| GET | `/api/affiliates/{id}` | Ver afiliado | Autenticado |
| PUT | `/api/affiliates/{id}` | Actualizar afiliado | ADMIN |
| DELETE | `/api/affiliates/{id}` | Eliminar afiliado | ADMIN |

### Solicitudes de Crédito (`/api/applications`)

| Método | Endpoint | Descripción | Roles Permitidos |
|--------|----------|-------------|------------------|
| POST | `/api/applications` | Crear solicitud | AFILIADO, ADMIN |
| GET | `/api/applications` | Listar solicitudes | Autenticado |
| GET | `/api/applications/{id}` | Ver solicitud | Autenticado |
| POST | `/api/applications/{id}/evaluate` | Evaluar solicitud | ANALISTA, ADMIN |

### Monitoreo (`/actuator`)

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado del sistema |
| `/actuator/metrics` | Métricas de la aplicación |
| `/actuator/prometheus` | Métricas formato Prometheus |
| `/actuator/info` | Información de la aplicación |

---

## 💡 Ejemplos de Uso

### 1. Crear un Afiliado

```bash
TOKEN="<admin-token>"

curl -X POST http://localhost:8080/api/affiliates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "document": "1234567890",
    "fullName": "Juan Pérez",
    "salary": 5000000,
    "affiliationDate": "2023-01-15"
  }'
```

### 2. Crear Solicitud de Crédito

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

### 3. Evaluar Solicitud

```bash
ANALYST_TOKEN="<analyst-token>"

curl -X POST http://localhost:8080/api/applications/1/evaluate \
  -H "Authorization: Bearer $ANALYST_TOKEN"
```

---

## 🔄 Flujo de Evaluación de Crédito

1. **Afiliado crea solicitud** → Estado: PENDIENTE
2. **Sistema llama a Risk Central** → Obtiene score y nivel de riesgo
3. **Aplica políticas internas:**
   - Score < 500 → RECHAZADO
   - Score >= 500 → APROBADO
4. **Actualiza solicitud** con resultado y evaluación

---

## 🧪 Pruebas

### 1. Pruebas Unitarias e Integración (JUnit 5 + Mockito)
El proyecto cuenta con una suite completa de pruebas automáticas que cubren:
- **Casos de Uso**: `RegisterCreditApplicationUseCase`, `EvaluateCreditApplicationUseCase`.
- **Controladores**: `CreditApplicationController` (con MockMvc).
- **Reglas de Negocio**: Validaciones de montos, antigüedad, capacidad de endeudamiento.

Para ejecutar las pruebas:
```bash
cd credit-application-service
mvn test
```

### 2. Script de Pruebas End-to-End
```bash
./test-api.sh
```
Este script prueba el flujo completo en un entorno desplegado:
- ✅ Autenticación (Admin, Analyst, Affiliate)
- ✅ Creación de afiliados
- ✅ Listado de afiliados
- ✅ Creación de solicitudes
- ✅ Evaluación de solicitudes
- ✅ Health checks

### 3. Reporte de Verificación
Para un detalle completo de cómo se cumple cada requerimiento del proyecto, consulta el archivo:
📄 [VERIFICATION_REPORT.md](VERIFICATION_REPORT.md)

---

## ☁️ Despliegue en Render.com

El proyecto incluye configuración lista para desplegar en Render.com usando Blueprints.

### Archivos de Despliegue
- `render.yaml`: Blueprint de infraestructura as code.
- `DEPLOY_RENDER.md`: Guía paso a paso para el despliegue.

Para desplegar, sigue las instrucciones detalladas en:
📄 [Guía de Despliegue en Render](DEPLOY_RENDER.md)

---

## 🛠️ Tecnologías

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

## 📊 Modelo de Datos

### Afiliado (Affiliate)
```json
{
  "id": 1,
  "document": "1234567890",
  "fullName": "Juan Pérez",
  "salary": 5000000.00,
  "affiliationDate": "2023-01-15",
  "status": "ACTIVE"
}
```

### Solicitud de Crédito (CreditApplication)
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

### Evaluación de Riesgo (RiskEvaluation)
```json
{
  "id": 1,
  "document": "1234567890",
  "score": 642,
  "riskLevel": "MEDIO",
  "detail": "Historial crediticio moderado",
  "evaluationDate": "2025-12-09T19:21:31.345"
}
```

---

## 🐳 Docker

### Comandos Útiles

```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Limpiar todo (incluye volúmenes)
docker-compose down -v

# Reconstruir imágenes
docker-compose up --build -d

# Ver estado
docker-compose ps
```

### Conexión a PostgreSQL

```bash
docker exec -it coopcredit-db psql -U postgres -d coopcredit_db
```

---

## 📈 Métricas y Observabilidad

### Verificar Estado del Sistema

```bash
curl http://localhost:8080/actuator/health | jq .
```

### Ver Métricas

```bash
# Métricas generales
curl http://localhost:8080/actuator/metrics | jq '.names'

# Métricas específicas
curl http://localhost:8080/actuator/metrics/http.server.requests | jq .
```

### Métricas Custom Implementadas

- `credit_applications_created_total` - Total de solicitudes creadas
- `credit_applications_evaluated_total` - Total de solicitudes evaluadas
- `http_request_duration_seconds` - Duración de requests HTTP

---

## 🔧 Configuración

### Variables de Entorno (application.yml)

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

## 🤝 Desarrolladores

- Arquitectura: Hexagonal (Ports & Adapters)
- Patrones: Repository, Use Case, DTO, Builder
- Principios: SOLID, Clean Code, DRY

---

## 📄 Licencia

Este proyecto es un ejemplo educativo para demostración de arquitectura de microservicios.

---

## 📞 Soporte

Para reportar issues o sugerencias, por favor abrir un ticket en el repositorio.
