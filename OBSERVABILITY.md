# Guía de Observabilidad - CoopCredit

## 📊 Configuración Completa de Observabilidad

### Actuator Endpoints Disponibles

Base URL: `http://localhost:8080/actuator`

| Endpoint | Descripción | Ejemplo |
|----------|-------------|---------|
| `/health` | Estado del sistema y componentes | Ver estado DB, disco, etc |
| `/info` | Información de la aplicación | Versión, build info |
| `/metrics` | Lista de métricas disponibles | 106+ métricas |
| `/metrics/{name}` | Métrica específica | `/metrics/jvm.memory.used` |
| `/prometheus` | Formato Prometheus | Para scraping |

---

## 🔍 Health Check

### Endpoint
```bash
GET http://localhost:8080/actuator/health
```

### Respuesta
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250375106560,
        "free": 102424031232,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Estados Posibles
- `UP` - Servicio funcionando correctamente
- `DOWN` - Servicio con problemas
- `OUT_OF_SERVICE` - Servicio deshabilitado
- `UNKNOWN` - Estado desconocido

---

## 📈 Métricas Personalizadas (Micrometer)

### Métricas de Negocio

#### 1. Solicitudes Creadas
```bash
GET /actuator/metrics/credit_applications_created_total
```

**Tipo**: Counter  
**Descripción**: Total de solicitudes de crédito creadas  
**Tags**: `status` (success/error)

```json
{
  "name": "credit_applications_created_total",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 7.0
    }
  ],
  "availableTags": [
    {
      "tag": "status",
      "values": ["success", "error"]
    }
  ]
}
```

#### 2. Solicitudes Evaluadas
```bash
GET /actuator/metrics/credit_applications_evaluated_total
```

**Tipo**: Counter  
**Descripción**: Total de solicitudes evaluadas  
**Tags**: `result` (approved/rejected)

#### 3. Duración de Evaluaciones
```bash
GET /actuator/metrics/credit_application_evaluation_duration
```

**Tipo**: Timer  
**Descripción**: Tiempo de procesamiento de evaluaciones  
**Estadísticas**: count, total_time, max, mean

#### 4. Fallos de Autenticación
```bash
GET /actuator/metrics/authentication_failures_total
```

**Tipo**: Counter  
**Descripción**: Intentos fallidos de autenticación

---

## 🎯 Métricas del Sistema (Spring Boot)

### HTTP Requests
```bash
GET /actuator/metrics/http.server.requests
```

**Tags disponibles:**
- `uri` - Endpoint accedido
- `method` - HTTP method (GET, POST, etc)
- `status` - HTTP status code
- `outcome` - SUCCESS, CLIENT_ERROR, SERVER_ERROR

**Ejemplo:**
```bash
curl 'http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/applications'
```

### JVM Métricas

#### Memoria
```bash
GET /actuator/metrics/jvm.memory.used
GET /actuator/metrics/jvm.memory.max
GET /actuator/metrics/jvm.memory.committed
```

#### Garbage Collection
```bash
GET /actuator/metrics/jvm.gc.pause
GET /actuator/metrics/jvm.gc.memory.allocated
```

#### Threads
```bash
GET /actuator/metrics/jvm.threads.live
GET /actuator/metrics/jvm.threads.peak
```

### Base de Datos (HikariCP)

```bash
GET /actuator/metrics/jdbc.connections.active
GET /actuator/metrics/jdbc.connections.max
GET /actuator/metrics/jdbc.connections.min
```

---

## 🔥 Prometheus Integration

### Endpoint
```bash
GET http://localhost:8080/actuator/prometheus
```

### Formato de Salida
```prometheus
# HELP credit_applications_created_total Total credit applications created
# TYPE credit_applications_created_total counter
credit_applications_created_total{application="credit-application-service",status="success",} 7.0

# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{application="credit-application-service",area="heap",id="G1 Eden Space",} 5.24288E7

# HELP http_server_requests_seconds  
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{application="credit-application-service",method="POST",status="200",uri="/api/applications",} 3.0
http_server_requests_seconds_sum{application="credit-application-service",method="POST",status="200",uri="/api/applications",} 0.156789
```

### Configuración Prometheus (prometheus.yml)
```yaml
scrape_configs:
  - job_name: 'coopcredit'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

---

## 🛠️ Configuración (application.yml)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
```

---

## 📊 Implementación de Métricas Custom

### Usando @Aspect (AOP)

```java
@Aspect
@Component
public class MonitoringAspect {
    
    private final MeterRegistry meterRegistry;
    
    // Counter: Solicitudes creadas
    @AfterReturning("execution(* com.coopcredit..usecase.RegisterCreditApplicationUseCase.execute(..))")
    public void incrementCreatedApplications() {
        meterRegistry.counter(
            "credit_applications_created_total",
            "status", "success"
        ).increment();
    }
    
    // Timer: Duración de evaluaciones
    @Around("execution(* com.coopcredit..usecase.EvaluateCreditApplicationUseCase.execute(..))")
    public Object timeEvaluation(ProceedingJoinPoint joinPoint) throws Throwable {
        return Timer.builder("credit_application_evaluation_duration")
            .register(meterRegistry)
            .recordCallable(() -> joinPoint.proceed());
    }
}
```

### Uso Directo en Código

```java
@Service
public class AuthenticationUseCase {
    
    private final Counter failureCounter;
    
    public AuthenticationUseCase(MeterRegistry registry) {
        this.failureCounter = Counter.builder("authentication_failures_total")
            .description("Failed authentication attempts")
            .register(registry);
    }
    
    public void login(LoginRequest request) {
        try {
            // ... lógica de autenticación
        } catch (BadCredentialsException e) {
            failureCounter.increment();
            throw e;
        }
    }
}
```

---

## 🚀 Comandos Útiles

### Verificar Health
```bash
curl http://localhost:8080/actuator/health | jq .
```

### Listar Todas las Métricas
```bash
curl http://localhost:8080/actuator/metrics | jq '.names'
```

### Ver Métrica Específica
```bash
curl http://localhost:8080/actuator/metrics/credit_applications_created_total | jq .
```

### Filtrar por Tags
```bash
curl 'http://localhost:8080/actuator/metrics/http.server.requests?tag=status:200&tag=method:POST' | jq .
```

### Export para Prometheus
```bash
curl http://localhost:8080/actuator/prometheus > metrics.txt
```

---

## 📈 Integración con Grafana

### Dashboard Recomendado

**Datasource**: Prometheus

**Paneles Sugeridos:**

1. **Solicitudes por Estado**
   - Query: `rate(credit_applications_created_total[5m])`
   - Tipo: Graph

2. **Tiempo de Evaluación**
   - Query: `credit_application_evaluation_duration_seconds`
   - Tipo: Heatmap

3. **HTTP Requests Rate**
   - Query: `rate(http_server_requests_seconds_count[1m])`
   - Tipo: Graph
   - Group by: uri, method

4. **Memory Usage**
   - Query: `jvm_memory_used_bytes{area="heap"}`
   - Tipo: Gauge

5. **Database Connections**
   - Query: `jdbc_connections_active`
   - Tipo: Gauge

---

## 🔍 Troubleshooting

### Endpoint No Disponible

**Problema**: 404 en `/actuator/metrics`

**Solución**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # O específicamente: health,metrics,prometheus
```

### Métricas No Aparecen

**Verificar**:
1. Dependency en `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

2. Bean de MeterRegistry inyectado correctamente

### Prometheus No Scrapping

**Verificar**:
1. Endpoint accesible: `curl http://localhost:8080/actuator/prometheus`
2. Configuración `prometheus.yml` correcta
3. Target en Prometheus UI muestra "UP"

---

## 📝 Best Practices

### Nombres de Métricas
- Usar snake_case: `credit_applications_created_total`
- Sufijos estándar:
  - `_total` para counters
  - `_duration_seconds` para timers
  - `_bytes` para tamaños

### Tags
- Usar para dimensiones: `status`, `type`, `result`
- Evitar alta cardinalidad: NO usar IDs únicos
- Consistencia entre métricas relacionadas

### Performance
- Counters son económicos
- Timers tienen overhead (usar solo donde importa)
- Evitar crear métricas dinámicamente en runtime

---

## ✅ Checklist de Observabilidad

- [x] Actuator habilitado
- [x] Health checks configurados
- [x] Métricas expuestas
- [x] Prometheus endpoint activo
- [x] Métricas custom implementadas
- [x] Tags configurados
- [x] Application name en métricas
- [] Grafana dashboards (opcional)
- [ ] Alertas configuradas (opcional)
- [ ] Distributed tracing (opcional)

---

## 🎯 Métricas Implementadas en CoopCredit

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `credit_applications_created_total` | Counter | Solicitudes creadas |
| `credit_applications_evaluated_total` | Counter | Solicitudes evaluadas |
| `credit_application_creation_duration` | Timer | Tiempo de creación |
| `credit_application_evaluation_duration` | Timer | Tiempo de evaluación |
| `authentication_failures_total` | Counter | Fallos de autenticación |
| `controller_method_duration` | Timer | Duración de métodos |

**Estado**: ✅ Todas funcionando y verificadas
