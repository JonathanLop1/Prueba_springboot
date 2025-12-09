# 🎉 Observabilidad CoopCredit - Guía Completa

## ✅ Sistema Completamente Configurado

Tu stack de observabilidad está **101% listo y funcionando!**

---

## 🌐 URLs de Acceso

| Servicio | URL | Credenciales | Estado |
|----------|-----|--------------|--------|
| **Prometheus** | http://localhost:19090 | No requiere | ✅ UP |
| **Grafana** | http://localhost:3000 | admin / admin | ✅ UP |
| **Application** | http://localhost:8080 | Ver README | ✅ UP |
| **Risk Central** | http://localhost:8081 | - | ✅ UP |

> **Nota**: Prometheus usa puerto **19090** (en lugar del estándar 9090) para evitar conflictos con otros servicios.

---

## 📊 Uso Rápido de Prometheus

### 1. Abrir Prometheus
- **URL**: http://localhost:19090
- Click en **Graph** en la barra superior

### 2. Queries de Ejemplo

Copia y pega estas queries en el campo de búsqueda:

```promql
# Ver solicitudes creadas totales
credit_applications_created_total

# TasaSolicitudes por segundo
rate(credit_applications_created_total[1m])

# Tiempo promedio de evaluación
rate(credit_application_evaluation_duration_seconds_sum[5m]) / 
rate(credit_application_evaluation_duration_seconds_count[5m])

# Memoria JVM usada
jvm_memory_used_bytes{area="heap"}

# HTTP Requests activos
http_server_requests_seconds_count

# Conexiones DB activas
jdbc_connections_active
```

### 3. Ver Targets
- Click en **Status** → **Targets**
- Verifica que `credit-application-service` esté **UP** (verde)

---

## 📈 Uso Rápido de Grafana

### 1. Login

1. Abrir http://localhost:3000
2. Usuario: `admin`
3. Password: `admin`
4. (Primera vez) Skip change password o cambiarla

### 2. Acceder al Dashboard

1. Click en **☰** (menú hamburguesa, top-left)
2. **Dashboards**
3. Click en **CoopCredit - Business Metrics**

### 3. Dashboard Incluye:

**8 Paneles Pre-configurados:**

1. 📊 **Solicitudes de Crédito (Rate)** - Creación y Evaluación/segundo
2. 🎯 **Total Solicitudes Creadas** - Counter total
3. 🎯 **Total Solicitudes Evaluadas** - Counter total
4. ⏱️ **Tiempo de Evaluación** - Latencia promedio
5. 🌐 **HTTP Requests por Endpoint** - Tráfico desglosado
6. 💾 **JVM Memory Usage** - Uso de memoria heap
7. 🗄️ **Conexiones DB Activas** - Pool de conexiones
8. ✅ **Application Status** - UP/DOWN

**Características:**
- ✅ Auto-refresh cada 5 segundos
- ✅ Filtros por status, method, etc.
- ✅ Visualizaciones en tiempo real
- ✅ Totalmente personalizable

---

## 🧪 Generar Tráfico para Ver Métricas

```bash
# Generar tráfico único
./test-api.sh

# Generar tráfico continuo (terminal separada)
while true; do 
  ./test-api.sh
  sleep 10
done
```

**Resultado**: Verás las métricas actualizándose en temps real en Grafana!

---

## 🎯 Cómo Monitorear en Tiempo Real

### Configuración Recomendada:

1. **Pantalla 1**: Editor de código
2. **Pantalla 2**: Grafana dashboard (http://localhost:3000)
   - Dashboard: CoopCredit - Business Metrics
   - Auto-refresh: 5s
3. **Terminal**: Ejecutar `./test-api.sh` cuando sea necesario

**Workflow**:
1. Haces cambios en el código
2. Rebuild con `docker-compose up --build -d`
3. Ejecutas `./test-api.sh`
4. Ves métricas actualizándose en Grafana
5. Analizas performance, errores, latencias

---

##  Queries PromQL Avanzadas

### Performance

```promql
# Percentil 95 de tiempos de respuesta
histogram_quantile(0.95,
  rate(http_server_requests_seconds_bucket[5m]))

# Percentil 99
histogram_quantile(0.99,
  rate(http_server_requests_seconds_bucket[5m]))

# Requests más lentos
topk(5,
  rate(http_server_requests_seconds_sum[5m]) /
  rate(http_server_requests_seconds_count[5m]))
```

### Errores

```promql
# Rate de errores 5xx
rate(http_server_requests_seconds_count{status=~"5.."}[1m])

# Rate de errores 4xx
rate(http_server_requests_seconds_count{status=~"4.."}[1m])

# Tasa de éxito
rate(http_server_requests_seconds_count{status=~"2.."}[1m]) /
rate(http_server_requests_seconds_count[1m])
```

### Recursos

```promql
# CPU usage
process_cpu_usage

# Threads JVM
jvm_threads_live
jvm_threads_daemon

# GC pause time
rate(jvm_gc_pause_seconds_sum[1m])

# DB connection pool
hikaricp_connections_active
hikaricp_connections_idle
```

---

## 🔧 Personalizar Grafana

### Agregar Nuevo Panel

1. En dashboard, click **Add** → **Visualization**
2. Selecciona datasource: **Prometheus**
3. Ingresa query PromQL (ej: `jvm_threads_live`)
4. Selecciona visualización:
   - **Time series**: Gráfica de líneas
   - **Gauge**: Medidor circular
   - **Stat**: Número grande
   - **Table**: Tabla de datos
5. Personaliza colores, umbrales, títulos
6. Click **Apply**

### Crear Alerta

1. En un panel, click **Edit**
2. Tab **Alert**
3. **Create alert rule from this panel**
4. Configurar condición:
   ```
   WHEN avg() OF query(A, 5m, now) IS ABOVE 0.5
   ```
5. Definir acción (email, Slack, Webhook)
6. **Save**

---

## 📱 Integración con Otros Servicios

### Exportar Dashboard

```bash
# Desde Grafana UI:
# Dashboard → Share → Export → Save to file
# Archivo: coopcredit-dashboard.json
```

### Importar Dashboard

```bash
# Grafana UI:
# + → Import → Upload JSON file
```

### Grafana API

```bash
# Get dashboard
curl -H "Authorization: Bearer YOUR_API_KEY" \
  http://localhost:3000/api/dashboards/uid/coopcredit-business

# Create alert
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d @alert-config.json \
  http://localhost:3000/api/alert-notifications
```

---

## 🛠️ Troubleshooting

### Prometheus no muestra datos

```bash
# 1. Verificar que Prometheus está UP
curl http://localhost:19090/-/healthy

# 2. Ver logs
docker logs prometheus

# 3. Verificar configuración
docker exec prometheus cat /etc/prometheus/prometheus.yml

# 4. Reiniciar
docker-compose restart prometheus
```

### Grafana muestra "No data"

**Solución 1: Verificar Datasource**
1. Configuration → Data Sources → Prometheus
2. URL debe ser: `http://prometheus:9090` (interno)
3. Click **Save & Test** - debe decir "Data source is working"

**Solución 2: Verificar query**
1. Ir a Prometheus: http://localhost:19090
2. Ejecutar mismo query
3. Si funciona en Prometheus pero no en Grafana → problema de datasource

**Solución 3: Generar datos**
```bash
./test-api.sh
```

### Dashboard no carga automáticamente

```bash
# Verificar archivos
ls -la grafana/provisioning/dashboards/

# Debe mostrar:
# - dashboard-provider.yml
# - coopcredit-business.json

# Reiniciar Grafana
docker-compose restart grafana

# Ver logs de provisioning
docker logs grafana | grep provisioning
```

---

## ✅ Verificación Completa

```bash
# Script de verificación todo-en-uno
echo "🔍 Verificando Stack de Observabilidad..."
echo ""

# Prometheus
echo -n "Prometheus: "
curl -s http://localhost:19090/-/healthy && echo "✅" || echo "❌"

# Grafana
echo -n "Grafana: "
curl -s http://localhost:3000/api/health | jq -r '.database' && echo "✅" || echo "❌"

# Application
echo -n "Application: "
curl -s http://localhost:8080/actuator/health | jq -r '.status' && echo "✅" || echo "❌"

# Métricas
echo -n "Métricas: "
curl -s 'http://localhost:19090/api/v1/query?query=up' | jq -r '.status' && echo "✅" || echo "❌"

echo ""
echo "🎉 Verificación completada!"
```

---

## 📚 Recursos Adicionales

### Documentación
- [Prometheus Docs](https://prometheus.io/docs/)
- [Grafana Docs](https://grafana.com/docs/)
- [PromQL Tutorial](https://prometheus.io/docs/prometheus/latest/querying/basics/)

### Dashboards Públicos
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)
- [Spring Boot Dashboards](https://grafana.com/grafana/dashboards/?search=spring+boot)

### Archivos del Proyecto
- Configuración Prometheus: `prometheus/prometheus.yml`
- Dashboard JSON: `grafana/provisioning/dashboards/coopcredit-business.json`
- Datasource: `grafana/provisioning/datasources/prometheus.yml`

---

## 🎯 Próximos Pasos

1. **✅ Sistema Funcionando** - Stack completo operativo
2. **📊 Explorar Métricas** - Probar queries en Prometheus
3. **📈 Personalizar Dashboard** - Agregar paneles según necesidades
4. **🔔 Configurar Alertas** - Notificaciones para eventos críticos
5. **🚀 Productizar** - Ajustes para entorno de producción

---

## 💡 Tips de Uso

1. **Deja Grafana abierto** mientras desarrollas para ver métricas en tiempo real
2. **Usa auto-refresh** de 5s para monitoreo continuo
3. **Explora PromQL** - Es muy poderoso una vez que aprendes la sintaxis
4. **Exporta dashboards** regularmente como backup
5. **Crea alertas** para métricas críticas (latencia, errores, etc.)

---

¡Tu observabilidad está 100% lista para monitorear CoopCredit en tiempo real! 🚀
