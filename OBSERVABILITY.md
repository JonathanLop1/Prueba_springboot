# Observability Guide - CoopCredit

## 📊 Complete Observability Configuration

### Available Actuator Endpoints

Base URL: `http://localhost:8080/actuator`

| Endpoint | Description | Example |
|----------|-------------|---------|
| `/health` | System and component status | View DB status, disk, etc |
| `/info` | Application information | Version, build info |
| `/metrics` | List of available metrics | 106+ metrics |
| `/metrics/{name}` | Specific metric | `/metrics/jvm.memory.used` |
| `/prometheus` | Prometheus format | For scraping |

---

## 🔍 Health Check

### Endpoint
```bash
GET http://localhost:8080/actuator/health
```

### Response
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

### Possible States
- `UP` - Service working correctly
- `DOWN` - Service with issues
- `OUT_OF_SERVICE` - Service disabled
- `UNKNOWN` - Unknown state

---

## 📈 Custom Metrics (Micrometer)

### Business Metrics

#### 1. Created Applications
```bash
GET /actuator/metrics/credit_applications_created_total
```

**Type**: Counter  
**Description**: Total credit applications created  
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

#### 2. Evaluated Applications
```bash
GET /actuator/metrics/credit_applications_evaluated_total
```

**Type**: Counter  
**Description**: Total applications evaluated  
**Tags**: `result` (approved/rejected)

#### 3. Evaluation Duration
```bash
GET /actuator/metrics/credit_application_evaluation_duration
```

**Type**: Timer  
**Description**: Evaluation processing time  
**Statistics**: count, total_time, max, mean

#### 4. Authentication Failures
```bash
GET /actuator/metrics/authentication_failures_total
```

**Type**: Counter  
**Description**: Failed authentication attempts

---

## 🎯 System Metrics (Spring Boot)

### HTTP Requests
```bash
GET /actuator/metrics/http.server.requests
```

**Available Tags:**
- `uri` - Accessed endpoint
- `method` - HTTP method (GET, POST, etc)
- `status` - HTTP status code
- `outcome` - SUCCESS, CLIENT_ERROR, SERVER_ERROR

**Example:**
```bash
curl 'http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/applications'
```

### JVM Metrics

#### Memory
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

### Database (HikariCP)

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

### Output Format
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

### Prometheus Configuration (prometheus.yml)
```yaml
scrape_configs:
  - job_name: 'coopcredit'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

---

## 🛠️ Configuration (application.yml)

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

## 📊 Custom Metrics Implementation

### Using @Aspect (AOP)

```java
@Aspect
@Component
public class MonitoringAspect {
    
    private final MeterRegistry meterRegistry;
    
    // Counter: Created applications
    @AfterReturning("execution(* com.coopcredit..usecase.RegisterCreditApplicationUseCase.execute(..))")
    public void incrementCreatedApplications() {
        meterRegistry.counter(
            "credit_applications_created_total",
            "status", "success"
        ).increment();
    }
    
    // Timer: Evaluation duration
    @Around("execution(* com.coopcredit..usecase.EvaluateCreditApplicationUseCase.execute(..))")
    public Object timeEvaluation(ProceedingJoinPoint joinPoint) throws Throwable {
        return Timer.builder("credit_application_evaluation_duration")
            .register(meterRegistry)
            .recordCallable(() -> joinPoint.proceed());
    }
}
```

### Direct Usage in Code

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
            // ... authentication logic
        } catch (BadCredentialsException e) {
            failureCounter.increment();
            throw e;
        }
    }
}
```

---

## 🚀 Useful Commands

### Verify Health
```bash
curl http://localhost:8080/actuator/health | jq .
```

### List All Metrics
```bash
curl http://localhost:8080/actuator/metrics | jq '.names'
```

### View Specific Metric
```bash
curl http://localhost:8080/actuator/metrics/credit_applications_created_total | jq .
```

### Filter by Tags
```bash
curl 'http://localhost:8080/actuator/metrics/http.server.requests?tag=status:200&tag=method:POST' | jq .
```

### Export for Prometheus
```bash
curl http://localhost:8080/actuator/prometheus > metrics.txt
```

---

## 📈 Integration with Grafana

### Recommended Dashboard

**Datasource**: Prometheus

**Suggested Panels:**

1. **Applications by Status**
   - Query: `rate(credit_applications_created_total[5m])`
   - Type: Graph

2. **Evaluation Time**
   - Query: `credit_application_evaluation_duration_seconds`
   - Type: Heatmap

3. **HTTP Requests Rate**
   - Query: `rate(http_server_requests_seconds_count[1m])`
   - Type: Graph
   - Group by: uri, method

4. **Memory Usage**
   - Query: `jvm_memory_used_bytes{area="heap"}`
   - Type: Gauge

5. **Database Connections**
   - Query: `jdbc_connections_active`
   - Type: Gauge

---

## 🔍 Troubleshooting

### Endpoint Not Available

**Problem**: 404 on `/actuator/metrics`

**Solution**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # Or specifically: health,metrics,prometheus
```

### Metrics Do Not Appear

**Verify**:
1. Dependency in `pom.xml`:
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

2. MeterRegistry Bean injected correctly

### Prometheus Not Scraping

**Verify**:
1. Endpoint accessible: `curl http://localhost:8080/actuator/prometheus`
2. `prometheus.yml` configuration correct
3. Target in Prometheus UI shows "UP"

---

## 📝 Best Practices

### Metric Names
- Use snake_case: `credit_applications_created_total`
- Standard suffixes:
  - `_total` for counters
  - `_duration_seconds` for timers
  - `_bytes` for sizes

### Tags
- Use for dimensions: `status`, `type`, `result`
- Avoid high cardinality: DO NOT use unique IDs
- Consistency between related metrics

### Performance
- Counters are cheap
- Timers have overhead (use only where it matters)
- Avoid creating metrics dynamically at runtime

---

## ✅ Observability Checklist

- [x] Actuator enabled
- [x] Health checks configured
- [x] Metrics exposed
- [x] Prometheus endpoint active
- [x] Custom metrics implemented
- [x] Tags configured
- [x] Application name in metrics
- [] Grafana dashboards (optional)
- [ ] Alerts configured (optional)
- [ ] Distributed tracing (optional)

---

## 🎯 Metrics Implemented in CoopCredit

| Metric | Type | Description |
|---------|------|-------------|
| `credit_applications_created_total` | Counter | Created applications |
| `credit_applications_evaluated_total` | Counter | Evaluated applications |
| `credit_application_creation_duration` | Timer | Creation time |
| `credit_application_evaluation_duration` | Timer | Evaluation time |
| `authentication_failures_total` | Counter | Authentication failures |
| `controller_method_duration` | Timer | Method duration |

**Status**: ✅ All working and verified
