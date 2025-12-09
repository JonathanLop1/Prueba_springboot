# Guía de Integración: Prometheus + Grafana

## 🚀 Inicio Rápido

### 1. Levantar el Stack Completo

```bash
# Detener servicios actuales (si están corriendo)
docker-compose down

# Iniciar todo incluyendo Prometheus y Grafana
docker-compose up -d

# Verificar que todos los servicios estén UP
docker-compose ps
```

Esperados:
- ✅ coopcredit-db (PostgreSQL) - Puerto 5432
- ✅ risk-central-mock - Puerto 8081
- ✅ credit-application - Puerto 8080
- ✅ prometheus - Puerto 9090
- ✅ grafana - Puerto 3000

---

## 📊 Acceso a las Interfaces

### Prometheus
- **URL**: http://localhost:19090
- **Autenticación**: No requiere
- **Función**: Motor de métricas y queries

### Grafana
- **URL**: http://localhost:3000
- **Usuario**: `admin`
- **Password**: `admin`
- **Función**: Dashboards y visualización

### Aplicación CoopCredit
- **URL**: http://localhost:8080
- **Métricas**: http://localhost:8080/actuator/prometheus

---

## 🎯 Paso a Paso: Uso de Prometheus

### 1. Verificar Targets

1. Abrir http://localhost:9090
2. Ir a **Status** → **Targets**
3. Verificar que `credit-application-service` esté **UP**

### 2. Ejecutar Queries

En el campo de búsqueda de Prometheus:

```promql
# Ver solicitudes creadas
credit_applications_created_total

# Rate de solicitudes en los últimos 5 minutos
rate(credit_applications_created_total[5m])

# Tiempo promedio de evaluación
rate(credit_application_evaluation_duration_seconds_sum[5m]) / 
rate(credit_application_evaluation_duration_seconds_count[5m])

# Requests HTTP por endpoint
http_server_requests_seconds_count

# Memoria JVM usada
jvm_memory_used_bytes{area="heap"}

# Conexiones DB activas
jdbc_connections_active
```

### 3. Crear Gráficas

1. Click en **Add Panel**
2. Ingresar query PromQL
3. Ajustar visualización (Line, Gauge, Table, etc.)
4. Guardar dashboard

---

## 📈 Paso a Paso: Uso de Grafana

### 1. Login Inicial

1. Abrir http://localhost:3000
2. Login con `admin` / `admin`
3. (Opcional) Cambiar contraseña o skip

### 2. Dashboard Pre-configurado

El dashboard **"CoopCredit - Business Metrics"** se carga automáticamente con:

**Panel 1: Solicitudes de Crédito (Rate)**
- Muestra tasa de creación y evaluación por segundo
- Auto-refresh cada 5 segundos

**Panel 2: Total Solicitudes Creadas**
- Gauge mostrando total acumulado
- Código de colores (green/yellow/red)

**Panel 3: Total Solicitudes Evaluadas**
- Gauge mostrando total acumulado

**Panel 4: Tiempo de Evaluación**
- Gráfica de tiempo promedio en segundos
- Incluye valores mean y max

**Panel 5: HTTP Requests por Endpoint**
- Desglose por método, URI y status
- Excluye endpoints actuator

**Panel 6: JVM Memory Usage**
- Uso de memoria heap
- Desglose por region (Eden, Old Gen, etc.)

**Panel 7: Conexiones DB Activas**
- Gauge de conexiones activas a PostgreSQL
- Thresholds: green <5, yellow 5-8, red >8

**Panel 8: Application Status**
- Estado UP/DOWN de la aplicación
- Background color coding

### 3. Explorar el Dashboard

1. Click en **Dashboards** → **Browse**
2. Seleccionar **CoopCredit - Business Metrics**
3. Interactuar:
   - Zoom en gráficas (click + drag)
   - Cambiar rango de tiempo (top-right)
   - Ver valores específicos (hover)

### 4. Personalizar

**Agregar Panel:**
1. Click **Add** → **Visualization**
2. Seleccionar datasource: **Prometheus**
3. Enter query PromQL
4. Elegir tipo de visualización
5. **Apply** para guardar

**Ejemplos de Queries Útiles:**

```promql
# Percentil 95 de tiempos de respuesta
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket[5m]))

# Errores HTTP 5xx
rate(http_server_requests_seconds_count{status=~"5.."}[5m])

# CPU usage
process_cpu_usage

# Threads activos
jvm_threads_live

# GC pause time
rate(jvm_gc_pause_seconds_sum[1m])
```

---

## 🔧 Configuración Avanzada

### Alertas en Grafana

1. En un panel, click **Edit**
2. Tab **Alert**
3. **Create Alert Rule**
4. Configurar condición:
   ```
   WHEN avg() OF query(A, 5m, now) IS ABOVE 0.5
   ```
5. Configurar notificación (email, Slack, etc.)

### Retención de Datos en Prometheus

Editar `prometheus/prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s
  
  # Retener datos por 30 días
storage:
  tsdb:
    retention.time: 30d
```

Recrear contenedor:
```bash
docker-compose up -d prometheus
```

---

## 📊 Queries PromQL Esenciales

### Métricas de Negocio

```promql
# Total de solicitudes creadas (Contador acumulado)
credit_applications_created_total

# Solicitudes por segundo (últimos 5 min)
rate(credit_applications_created_total[5m])

# Solicitudes en la última hora
increase(credit_applications_created_total[1h])

# Tasa de éxito vs fallos
rate(credit_applications_created_total{status="success"}[5m]) /
rate(credit_applications_created_total[5m])

# Tiempo promedio de evaluación
rate(credit_application_evaluation_duration_seconds_sum[5m]) /
rate(credit_application_evaluation_duration_seconds_count[5m])

# Percentil 99 de tiempo de evaluación
histogram_quantile(0.99,
  rate(credit_application_evaluation_duration_seconds_bucket[5m]))
```

### Métricas de Sistema

```promql
# Requests por segundo
rate(http_server_requests_seconds_count[1m])

# Requests por endpoint y status
sum by(uri, status) (rate(http_server_requests_seconds_count[5m]))

# Latencia promedio
rate(http_server_requests_seconds_sum[5m]) /
rate(http_server_requests_seconds_count[5m])

# Memoria heap usada (MB)
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# Memoria heap disponible (%)
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100

# Threads JVM
jvm_threads_live

# GC time por segundo
rate(jvm_gc_pause_seconds_sum[1m])

# Conexiones DB
jdbc_connections_active
jdbc_connections_max
```

---

## 🛠️ Troubleshooting

### Problema: Prometheus no scrappea métricas

**Síntoma**: Targets en estado DOWN

**Soluciones**:
1. Verificar que credit-application esté UP:
   ```bash
   docker-compose ps credit-application
   ```

2. Verificar endpoint de métricas:
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

3. Verificar configuración de Prometheus:
   ```bash
   docker exec prometheus cat /etc/prometheus/prometheus.yml
   ```

4. Ver logs de Prometheus:
   ```bash
   docker logs prometheus
   ```

### Problema: Grafana no muestra datos

**Síntoma**: Paneles vacíos "No data"

**Soluciones**:
1. Verificar datasource:
   - Configuration → Data Sources → Prometheus
   - Click "Test" - debe decir "Data source is working"

2. Verificar que Prometheus tenga datos:
   - Abrir http://localhost:9090
   - Query: `up`
   - Debe mostrar valor 1

3. Verificar rango de tiempo:
   - Top-right en Grafana
   - Cambiar a "Last 5 minutes"

4. Generar tráfico:
   ```bash
   ./test-api.sh
   ```

### Problema: Dashboard no se carga automáticamente

**Solución**:
```bash
# Verificar archivos de provisioning
ls -la grafana/provisioning/dashboards/
ls -la grafana/provisioning/datasources/

# Reiniciar Grafana
docker-compose restart grafana

# Ver logs
docker logs grafana
```

---

## 🎯 Escenarios de Monitoreo

### 1. Monitoreo en Tiempo Real Durante Desarrollo

```bash
# Terminal 1: Generar tráfico
while true; do ./test-api.sh; sleep 10; done

# Terminal 2: Ver métricas en vivo
watch -n 2 'curl -s http://localhost:8080/actuator/metrics/credit_applications_created_total | jq'

# Browser: Abrir Grafana y ver dashboard actualizándose
```

### 2. Análisis de Performance

**En Prometheus**:
```promql
# Top 5 endpoints más lentos
topk(5, 
  rate(http_server_requests_seconds_sum[5m]) / 
  rate(http_server_requests_seconds_count[5m])
)

# Distribución de tiempos de respuesta
histogram_quantile(0.50, 
  rate(http_server_requests_seconds_bucket[5m])) as "p50",
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket[5m])) as "p95",
histogram_quantile(0.99, 
  rate(http_server_requests_seconds_bucket[5m])) as "p99"
```

### 3. Monitoreo de Salud del Sistema

Dashboard recomendado con alertas:
- CPU > 80% por 5 min
- Memoria > 85% por 3 min
- Conexiones DB > 8 activas
- Error rate > 5% por 2 min
- Latencia p99 > 1s por 5 min

---

## 📱 Próximos Pasos

1. **Configurar Alertas**:
   - Email para errores críticos
   - Slack para warnings

2. **Dashboards Adicionales**:
   - JVM details
   - Database performance
   - Business KPIs

3. **Integrar con Loki** (logs):
   ```yaml
   loki:
     image: grafana/loki:latest
     ports:
       - "3100:3100"
   ```

4. **Distributed Tracing con Jaeger**:
   - Ver flujo completo de requests
   - Identificar cuellos de botella

---

## ✅ Checklist Final

- [ ] Docker Compose con Prometheus y Grafana
- [ ] Prometheus scrapeando métricas
- [ ] Grafana accesible en puerto 3000
- [ ] Dashboard pre-configurado cargado
- [ ] Datos visibles en paneles
- [ ] Auto-refresh funcionando
- [ ] Queries PromQL probadas
- [ ] Alertas configuradas (opcional)

---

## 🎓 Recursos de Aprendizaje

**Prometheus**:
- https://prometheus.io/docs/prometheus/latest/querying/basics/

**Grafana**:
- https://grafana.com/docs/grafana/latest/

**PromQL Cheat Sheet**:
- https://promlabs.com/promql-cheat-sheet/

**Ejemplo de Dashboards**:
- https://grafana.com/grafana/dashboards/

---

¡Tu stack de observabilidad está completo y listo para producción! 🚀
