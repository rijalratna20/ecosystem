# primus-monitoring

Observability service with metrics, logs, traces, and alerting.

## Overview

`primus-monitoring` provides comprehensive observability across Primus services through metrics, structured logging, distributed tracing, and alerting, enabling operators to understand system behavior and detect issues quickly.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 6 - Phase 2)**

**Target Launch**: Week 15-18 of Phase 2  
**Phase**: v1.1 (Production Hardening) delivery  
**Priority**: HIGH - Enables operations monitoring  

## Purpose

Provides production visibility:
- **Metrics**: Latency, throughput, error rates
- **Logs**: Structured, searchable, correlated
- **Traces**: End-to-end request flows
- **Alerts**: SLA breaches, performance degradation

## Key Features (Planned)

### Metrics (Prometheus)
```
primus_export_duration_seconds{percentile="p95"}
primus_export_errors_total
primus_retrieval_latency_seconds
primus_storage_read_bytes_total
primus_storage_backend_healthy{backend="s3"}
```

### Logs (ELK Stack)
```
{
  "timestamp": "2026-07-23T10:00:00.123Z",
  "level": "INFO",
  "service": "primus-server",
  "message": "Export completed",
  "correlationId": "req_abc123",
  "exportId": "exp_123456",
  "duration_ms": 1234,
  "status": "SUCCESS"
}
```

### Traces (Jaeger)
```
GET /api/v1/exports/123
├─ Authenticate user (10ms)
├─ Load metadata (20ms)
├─ Query database (100ms)
└─ Format response (5ms)
Total: 135ms
```

### Dashboards (Grafana)
- Export/retrieval latency trends
- Error rate by service
- Storage usage and trends
- API gateway traffic
- User activity

### Alerts
- p95 latency > 30s: WARNING
- p95 latency > 60s: CRITICAL
- Error rate > 1%: WARNING
- Error rate > 5%: CRITICAL
- Storage quota > 80%: WARNING

## Configuration (Planned)

```properties
# Prometheus
primus.metrics.enabled=true
primus.metrics.export-interval=60s
primus.metrics.port=9090

# Logging
primus.logging.level=INFO
primus.logging.format=json
primus.logging.elasticsearch.enabled=true
primus.logging.elasticsearch.host=elasticsearch:9200

# Tracing
primus.tracing.enabled=true
primus.tracing.jaeger.endpoint=http://jaeger:14268/api/traces
primus.tracing.sample-rate=0.1

# Alerts
primus.alerts.enabled=true
primus.alerts.alertmanager.host=alertmanager:9093
```

## Metrics Taxonomy (Planned)

### Request Metrics
- `primus_http_requests_total` — Total HTTP requests
- `primus_http_request_duration_seconds` — Request latency
- `primus_http_request_size_bytes` — Request payload size
- `primus_http_response_size_bytes` — Response payload size

### Business Metrics
- `primus_exports_total` — Total exports by status
- `primus_exports_duration_seconds` — Export latency
- `primus_retrievals_total` — Total retrievals
- `primus_masked_fields_total` — Sensitive fields masked

### Storage Metrics
- `primus_storage_bytes_written_total` — Data written
- `primus_storage_bytes_read_total` — Data read
- `primus_storage_operations_total` — PUT/GET/DELETE count
- `primus_storage_backend_latency_seconds` — Storage latency

### System Metrics
- `primus_database_connections` — Active connections
- `primus_cache_hits_total` — Cache hit rate
- `jvm_memory_used_bytes` — JVM memory
- `jvm_gc_duration_seconds` — Garbage collection time

## Alerting Rules (Planned)

```yaml
groups:
  - name: primus_alerts
    rules:
      - alert: HighExportLatency
        expr: histogram_quantile(0.95, primus_export_duration_seconds) > 30
        for: 5m
        annotations:
          summary: "Export latency high (p95 > 30s)"
      
      - alert: HighErrorRate
        expr: rate(primus_http_requests_total{status="500"}[5m]) > 0.01
        for: 5m
        annotations:
          summary: "Error rate > 1%"
      
      - alert: StorageQuotaWarning
        expr: primus_storage_usage_bytes / primus_storage_capacity_bytes > 0.8
        annotations:
          summary: "Storage usage > 80%"
```

## Integration Points

- **primus-server** — Emits metrics/traces
- **primus-export** — Latency/throughput metrics
- **primus-retrieval** — Access patterns
- **primus-storage** — Backend performance
- **primus-audit** — Event counts

## Testing Strategy

### Unit Tests
- Metric collection
- Log formatting

### Integration Tests
- End-to-end metric capture
- Trace continuity
- Alert triggering

### Load Tests
- Metrics collection under load
- Log ingest performance

## Module Dependencies

- `primus-common` — shared types
- Prometheus client library
- Jaeger client library
- Elasticsearch client (optional)

## Deployment Notes

- Requires Prometheus scrape endpoints
- Elasticsearch for log storage (optional but recommended)
- Jaeger for distributed tracing
- Grafana for dashboards
- AlertManager for alerting

## Related Modules

- **primus-server** (Core) — Metric/trace source
- **primus-config** (Tier 7) — Dynamic alert configuration

---

**Tier**: 6 (Observability - Phase 2)  
**Status**: 🟠 NOT STARTED  
**Priority**: HIGH  
**Target Start**: September 20, 2026  
**Target Completion**: October 4, 2026

