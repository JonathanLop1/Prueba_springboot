# 🎉 CoopCredit Observability - Complete Guide

## ✅ System Fully Configured

Your observability stack is **101% ready and working!**

---

## 🌐 Access URLs

| Service | URL | Credentials | Status |
|----------|-----|--------------|--------|
| **Prometheus** | http://localhost:19090 | Not required | ✅ UP |
| **Grafana** | http://localhost:3000 | admin / admin | ✅ UP |
| **Application** | http://localhost:8080 | See README | ✅ UP |
| **Risk Central** | http://localhost:8081 | - | ✅ UP |

> **Note**: Prometheus uses port **19090** (instead of standard 9090) to avoid conflicts with other services.

---

## 📊 Quick Prometheus Usage

### 1. Open Prometheus
- **URL**: http://localhost:19090
- Click on **Graph** in the top bar

### 2. Example Queries

Copy and paste these queries into the search field:

```promql
# View total created applications
credit_applications_created_total

# Application rate per second
rate(credit_applications_created_total[1m])

# Average evaluation time
rate(credit_application_evaluation_duration_seconds_sum[5m]) / 
rate(credit_application_evaluation_duration_seconds_count[5m])

# JVM Memory used
jvm_memory_used_bytes{area="heap"}

# Active HTTP Requests
http_server_requests_seconds_count

# Active DB Connections
jdbc_connections_active
```

### 3. View Targets
- Click on **Status** → **Targets**
- Verify that `credit-application-service` is **UP** (green)

---

## 📈 Quick Grafana Usage

### 1. Login

1. Open http://localhost:3000
2. User: `admin`
3. Password: `admin`
4. (First time) Skip change password or change it

### 2. Access Dashboard

1. Click on **☰** (hamburger menu, top-left)
2. **Dashboards**
3. Click on **CoopCredit - Business Metrics**

### 3. Dashboard Includes:

**8 Pre-configured Panels:**

1. 📊 **Credit Applications (Rate)** - Creation and Evaluation/second
2. 🎯 **Total Applications Created** - Total Counter
3. 🎯 **Total Applications Evaluated** - Total Counter
4. ⏱️ **Evaluation Time** - Average latency
5. 🌐 **HTTP Requests by Endpoint** - Broken down traffic
6. 💾 **JVM Memory Usage** - Heap memory usage
7. 🗄️ **Active DB Connections** - Connection pool
8. ✅ **Application Status** - UP/DOWN

**Features:**
- ✅ Auto-refresh every 5 seconds
- ✅ Filters by status, method, etc.
- ✅ Real-time visualizations
- ✅ Fully customizable

---

## 🧪 Generate Traffic to View Metrics

```bash
# Generate single traffic
./test-api.sh

# Generate continuous traffic (separate terminal)
while true; do 
  ./test-api.sh
  sleep 10
done
```

**Result**: You will see metrics updating in real-time in Grafana!

---

## 🎯 How to Monitor in Real-Time

### Recommended Configuration:

1. **Screen 1**: Code editor
2. **Screen 2**: Grafana dashboard (http://localhost:3000)
   - Dashboard: CoopCredit - Business Metrics
   - Auto-refresh: 5s
3. **Terminal**: Run `./test-api.sh` when needed

**Workflow**:
1. Make changes in code
2. Rebuild with `docker-compose up --build -d`
3. Run `./test-api.sh`
4. Watch metrics updating in Grafana
5. Analyze performance, errors, latencies

---

##  Advanced PromQL Queries

### Performance

```promql
# 95th Percentile of response times
histogram_quantile(0.95,
  rate(http_server_requests_seconds_bucket[5m]))

# 99th Percentile
histogram_quantile(0.99,
  rate(http_server_requests_seconds_bucket[5m]))

# Slowest requests
topk(5,
  rate(http_server_requests_seconds_sum[5m]) /
  rate(http_server_requests_seconds_count[5m]))
```

### Errors

```promql
# 5xx Error Rate
rate(http_server_requests_seconds_count{status=~"5.."}[1m])

# 4xx Error Rate
rate(http_server_requests_seconds_count{status=~"4.."}[1m])

# Success Rate
rate(http_server_requests_seconds_count{status=~"2.."}[1m]) /
rate(http_server_requests_seconds_count[1m])
```

### Resources

```promql
# CPU usage
process_cpu_usage

# JVM Threads
jvm_threads_live
jvm_threads_daemon

# GC pause time
rate(jvm_gc_pause_seconds_sum[1m])

# DB connection pool
hikaricp_connections_active
hikaricp_connections_idle
```

---

## 🔧 Customize Grafana

### Add New Panel

1. In dashboard, click **Add** → **Visualization**
2. Select datasource: **Prometheus**
3. Enter PromQL query (e.g., `jvm_threads_live`)
4. Select visualization:
   - **Time series**: Line graph
   - **Gauge**: Circular meter
   - **Stat**: Big number
   - **Table**: Data table
5. Customize colors, thresholds, titles
6. Click **Apply**

### Create Alert

1. In a panel, click **Edit**
2. Tab **Alert**
3. **Create alert rule from this panel**
4. Configure condition:
   ```
   WHEN avg() OF query(A, 5m, now) IS ABOVE 0.5
   ```
5. Define action (email, Slack, Webhook)
6. **Save**

---

## 📱 Integration with Other Services

### Export Dashboard

```bash
# From Grafana UI:
# Dashboard → Share → Export → Save to file
# File: coopcredit-dashboard.json
```

### Import Dashboard

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

### Prometheus shows no data

```bash
# 1. Verify Prometheus is UP
curl http://localhost:19090/-/healthy

# 2. View logs
docker logs prometheus

# 3. Verify configuration
docker exec prometheus cat /etc/prometheus/prometheus.yml

# 4. Restart
docker-compose restart prometheus
```

### Grafana shows "No data"

**Solution 1: Verify Datasource**
1. Configuration → Data Sources → Prometheus
2. URL must be: `http://prometheus:9090` (internal)
3. Click **Save & Test** - should say "Data source is working"

**Solution 2: Verify query**
1. Go to Prometheus: http://localhost:19090
2. Execute same query
3. If it works in Prometheus but not in Grafana → datasource issue

**Solution 3: Generate data**
```bash
./test-api.sh
```

### Dashboard does not load automatically

```bash
# Verify files
ls -la grafana/provisioning/dashboards/

# Should show:
# - dashboard-provider.yml
# - coopcredit-business.json

# Restart Grafana
docker-compose restart grafana

# View provisioning logs
docker logs grafana | grep provisioning
```

---

## ✅ Complete Verification

```bash
# All-in-one verification script
echo "🔍 Verifying Observability Stack..."
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

# Metrics
echo -n "Metrics: "
curl -s 'http://localhost:19090/api/v1/query?query=up' | jq -r '.status' && echo "✅" || echo "❌"

echo ""
echo "🎉 Verification completed!"
```

---

## 📚 Additional Resources

### Documentation
- [Prometheus Docs](https://prometheus.io/docs/)
- [Grafana Docs](https://grafana.com/docs/)
- [PromQL Tutorial](https://prometheus.io/docs/prometheus/latest/querying/basics/)

### Public Dashboards
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)
- [Spring Boot Dashboards](https://grafana.com/grafana/dashboards/?search=spring+boot)

### Project Files
- Prometheus Configuration: `prometheus/prometheus.yml`
- Dashboard JSON: `grafana/provisioning/dashboards/coopcredit-business.json`
- Datasource: `grafana/provisioning/datasources/prometheus.yml`

---

## 🎯 Next Steps

1. **✅ System Working** - Full stack operational
2. **📊 Explore Metrics** - Test queries in Prometheus
3. **📈 Customize Dashboard** - Add panels as needed
4. **🔔 Configure Alerts** - Notifications for critical events
5. **🚀 Productize** - Adjustments for production environment

---

## 💡 Usage Tips

1. **Leave Grafana open** while developing to see real-time metrics
2. **Use auto-refresh** of 5s for continuous monitoring
3. **Explore PromQL** - It's very powerful once you learn the syntax
4. **Export dashboards** regularly as backup
5. **Create alerts** for critical metrics (latency, errors, etc.)

---

Your observability is 100% ready to monitor CoopCredit in real-time! 🚀
