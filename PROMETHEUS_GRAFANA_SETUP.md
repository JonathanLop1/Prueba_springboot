# Integration Guide: Prometheus + Grafana

## 🚀 Quick Start

### 1. Start the Full Stack

```bash
# Stop current services (if running)
docker-compose down

# Start everything including Prometheus and Grafana
docker-compose up -d

# Verify that all services are UP
docker-compose ps
```

Expected:
- ✅ coopcredit-db (PostgreSQL) - Port 5432
- ✅ risk-central-mock - Port 8081
- ✅ credit-application - Port 8080
- ✅ prometheus - Port 9090
- ✅ grafana - Port 3000

---

## 📊 Access to Interfaces

### Prometheus
- **URL**: http://localhost:19090
- **Authentication**: Not required
- **Function**: Metrics engine and queries

### Grafana
- **URL**: http://localhost:3000
- **User**: `admin`
- **Password**: `admin`
- **Function**: Dashboards and visualization

### CoopCredit Application
- **URL**: http://localhost:8080
- **Metrics**: http://localhost:8080/actuator/prometheus

---

## 🎯 Step by Step: Using Prometheus

### 1. Verify Targets

1. Open http://localhost:9090
2. Go to **Status** → **Targets**
3. Verify that `credit-application-service` is **UP**

### 2. Execute Queries

In the Prometheus search field:

```promql
# View created applications
credit_applications_created_total

# Application rate in the last 5 minutes
rate(credit_applications_created_total[5m])

# Average evaluation time
rate(credit_application_evaluation_duration_seconds_sum[5m]) / 
rate(credit_application_evaluation_duration_seconds_count[5m])

# HTTP Requests by endpoint
http_server_requests_seconds_count

# JVM Memory used
jvm_memory_used_bytes{area="heap"}

# Active DB connections
jdbc_connections_active
```

### 3. Create Graphs

1. Click on **Add Panel**
2. Enter PromQL query
3. Adjust visualization (Line, Gauge, Table, etc.)
4. Save dashboard

---

## 📈 Step by Step: Using Grafana

### 1. Initial Login

1. Open http://localhost:3000
2. Login with `admin` / `admin`
3. (Optional) Change password or skip

### 2. Pre-configured Dashboard

The **"CoopCredit - Business Metrics"** dashboard loads automatically with:

**Panel 1: Credit Applications (Rate)**
- Shows creation and evaluation rate per second
- Auto-refresh every 5 seconds

**Panel 2: Total Applications Created**
- Gauge showing accumulated total
- Color code (green/yellow/red)

**Panel 3: Total Applications Evaluated**
- Gauge showing accumulated total

**Panel 4: Evaluation Time**
- Graph of average time in seconds
- Includes mean and max values

**Panel 5: HTTP Requests by Endpoint**
- Breakdown by method, URI, and status
- Excludes actuator endpoints

**Panel 6: JVM Memory Usage**
- Heap memory usage
- Breakdown by region (Eden, Old Gen, etc.)

**Panel 7: Active DB Connections**
- Gauge of active connections to PostgreSQL
- Thresholds: green <5, yellow 5-8, red >8

**Panel 8: Application Status**
- UP/DOWN status of the application
- Background color coding

### 3. Explore the Dashboard

1. Click on **Dashboards** → **Browse**
2. Select **CoopCredit - Business Metrics**
3. Interact:
   - Zoom in graphs (click + drag)
   - Change time range (top-right)
   - View specific values (hover)

### 4. Customize

**Add Panel:**
1. Click **Add** → **Visualization**
2. Select datasource: **Prometheus**
3. Enter PromQL query
4. Choose visualization type
5. **Apply** to save

**Useful Query Examples:**

```promql
# 95th Percentile of response times
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket[5m]))

# HTTP 5xx Errors
rate(http_server_requests_seconds_count{status=~"5.."}[5m])

# CPU usage
process_cpu_usage

# Active threads
jvm_threads_live

# GC pause time
rate(jvm_gc_pause_seconds_sum[1m])
```

---

## 🔧 Advanced Configuration

### Alerts in Grafana

1. In a panel, click **Edit**
2. Tab **Alert**
3. **Create Alert Rule**
4. Configure condition:
   ```
   WHEN avg() OF query(A, 5m, now) IS ABOVE 0.5
   ```
5. Configure notification (email, Slack, etc.)

### Data Retention in Prometheus

Edit `prometheus/prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s
  
  # Retain data for 30 days
storage:
  tsdb:
    retention.time: 30d
```

Recreate container:
```bash
docker-compose up -d prometheus
```

---

## 📊 Essential PromQL Queries

### Business Metrics

```promql
# Total applications created (Accumulated counter)
credit_applications_created_total

# Applications per second (last 5 min)
rate(credit_applications_created_total[5m])

# Applications in the last hour
increase(credit_applications_created_total[1h])

# Success rate vs failures
rate(credit_applications_created_total{status="success"}[5m]) /
rate(credit_applications_created_total[5m])

# Average evaluation time
rate(credit_application_evaluation_duration_seconds_sum[5m]) /
rate(credit_application_evaluation_duration_seconds_count[5m])

# 99th Percentile of evaluation time
histogram_quantile(0.99,
  rate(credit_application_evaluation_duration_seconds_bucket[5m]))
```

### System Metrics

```promql
# Requests per second
rate(http_server_requests_seconds_count[1m])

# Requests by endpoint and status
sum by(uri, status) (rate(http_server_requests_seconds_count[5m]))

# Average latency
rate(http_server_requests_seconds_sum[5m]) /
rate(http_server_requests_seconds_count[5m])

# Heap memory used (MB)
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# Available heap memory (%)
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100

# JVM Threads
jvm_threads_live

# GC time per second
rate(jvm_gc_pause_seconds_sum[1m])

# DB Connections
jdbc_connections_active
jdbc_connections_max
```

---

## 🛠️ Troubleshooting

### Problem: Prometheus not scraping metrics

**Symptom**: Targets in DOWN state

**Solutions**:
1. Verify that credit-application is UP:
   ```bash
   docker-compose ps credit-application
   ```

2. Verify metrics endpoint:
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

3. Verify Prometheus configuration:
   ```bash
   docker exec prometheus cat /etc/prometheus/prometheus.yml
   ```

4. View Prometheus logs:
   ```bash
   docker logs prometheus
   ```

### Problem: Grafana not showing data

**Symptom**: Empty panels "No data"

**Solutions**:
1. Verify datasource:
   - Configuration → Data Sources → Prometheus
   - Click "Test" - should say "Data source is working"

2. Verify that Prometheus has data:
   - Open http://localhost:9090
   - Query: `up`
   - Should show value 1

3. Verify time range:
   - Top-right in Grafana
   - Change to "Last 5 minutes"

4. Generate traffic:
   ```bash
   ./test-api.sh
   ```

### Problem: Dashboard does not load automatically

**Solution**:
```bash
# Verify provisioning files
ls -la grafana/provisioning/dashboards/
ls -la grafana/provisioning/datasources/

# Restart Grafana
docker-compose restart grafana

# View logs
docker logs grafana
```

---

## 🎯 Monitoring Scenarios

### 1. Real-Time Monitoring During Development

```bash
# Terminal 1: Generate traffic
while true; do ./test-api.sh; sleep 10; done

# Terminal 2: View live metrics
watch -n 2 'curl -s http://localhost:8080/actuator/metrics/credit_applications_created_total | jq'

# Browser: Open Grafana and view dashboard updating
```

### 2. Performance Analysis

**In Prometheus**:
```promql
# Top 5 slowest endpoints
topk(5, 
  rate(http_server_requests_seconds_sum[5m]) / 
  rate(http_server_requests_seconds_count[5m])
)

# Response time distribution
histogram_quantile(0.50, 
  rate(http_server_requests_seconds_bucket[5m])) as "p50",
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket[5m])) as "p95",
histogram_quantile(0.99, 
  rate(http_server_requests_seconds_bucket[5m])) as "p99"
```

### 3. System Health Monitoring

Recommended dashboard with alerts:
- CPU > 80% for 5 min
- Memory > 85% for 3 min
- DB Connections > 8 active
- Error rate > 5% for 2 min
- Latency p99 > 1s for 5 min

---

## 📱 Next Steps

1. **Configure Alerts**:
   - Email for critical errors
   - Slack for warnings

2. **Additional Dashboards**:
   - JVM details
   - Database performance
   - Business KPIs

3. **Integrate with Loki** (logs):
   ```yaml
   loki:
     image: grafana/loki:latest
     ports:
       - "3100:3100"
   ```

4. **Distributed Tracing with Jaeger**:
   - View full request flow
   - Identify bottlenecks

---

## ✅ Final Checklist

- [ ] Docker Compose with Prometheus and Grafana
- [ ] Prometheus scraping metrics
- [ ] Grafana accessible on port 3000
- [ ] Pre-configured dashboard loaded
- [ ] Data visible in panels
- [ ] Auto-refresh working
- [ ] PromQL queries tested
- [ ] Alerts configured (optional)

---

## 🎓 Learning Resources

**Prometheus**:
- https://prometheus.io/docs/prometheus/latest/querying/basics/

**Grafana**:
- https://grafana.com/docs/grafana/latest/

**PromQL Cheat Sheet**:
- https://promlabs.com/promql-cheat-sheet/

**Dashboard Examples**:
- https://grafana.com/grafana/dashboards/

---

Your observability stack is complete and ready for production! 🚀
