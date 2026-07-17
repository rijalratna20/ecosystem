# Deployment Guide

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Load Balancer (SSL/TLS)                      │
└────────────────┬─────────────────┬─────────────────┬────────────┘
                 │                 │                 │
    ┌────────────▼──┐  ┌──────────▼──┐  ┌──────────▼──┐
    │   API Server  │  │ API Server  │  │ API Server  │
    │   Instance 1  │  │ Instance 2  │  │ Instance 3  │
    └────────────┬──┘  └──────────┬──┘  └──────────┬──┘
                 │                │                 │
    ┌────────────▼──────────────────────────────────▼──┐
    │        Shared Storage (NAS/S3/RDS)              │
    └──────────────────────────────────────────────────┘
```

## Prerequisites

### System Requirements

- **Java**: JDK 17 or higher
- **Gradle**: 8.0+ (or use wrapper)
- **Docker**: 20.10+ (for containerized deployment)
- **Kubernetes**: 1.20+ (for K8s deployment)
- **PostgreSQL**: 12+ (optional, for database storage)

### Network Requirements

- HTTPS/TLS enabled
- Outbound access to artifact repositories (Maven Central, etc.)
- Access to storage backends (NAS, S3, etc.)
- WebSocket support (for real-time status updates)

## Building for Deployment

### 1. Prepare Build

```bash
cd primus-platform

# Clean previous builds
./gradlew clean

# Build all modules
./gradlew build -Penv=production

# Run tests
./gradlew test
```

### 2. Create Distribution Package

```bash
# Build application distribution
./gradlew installDist

# Output: build/install/primus-platform/

# Or create a fat JAR
./gradlew shadowJar
```

### 3. Build Docker Images

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/install/primus-platform/ .

ENV JAVA_OPTS="-Xmx2g -Xms1g"

EXPOSE 8080 8443

CMD ["bin/primus"]
```

Build image:
```bash
docker build -t primus-platform:1.0.0 .
docker tag primus-platform:1.0.0 docker.company.com/primus-platform:1.0.0
docker push docker.company.com/primus-platform:1.0.0
```

## Deployment Methods

### 1. Standalone Server Deployment

**Best for**: Small to medium deployments

```bash
# Extract distribution
tar -xzf primus-platform-1.0.0.tar.gz
cd primus-platform-1.0.0

# Configure application
export JAVA_OPTS="-Xmx4g -Xms2g"
export PRIMUS_ENV=production
export PRIMUS_CONFIG=/etc/primus/application.yml

# Start service
bin/primus &
```

### 2. Docker Container Deployment

**Best for**: Development and staging

```bash
docker run -d \
  --name primus-platform \
  -p 8080:8080 \
  -p 8443:8443 \
  -e JAVA_OPTS="-Xmx2g" \
  -e PRIMUS_ENV=production \
  -v /etc/primus:/etc/primus \
  -v /data/primus:/data/primus \
  primus-platform:1.0.0
```

### 3. Kubernetes Deployment

**Best for**: Production, high-availability

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: primus-platform
spec:
  replicas: 3
  selector:
    matchLabels:
      app: primus-platform
  template:
    metadata:
      labels:
        app: primus-platform
    spec:
      containers:
      - name: primus
        image: docker.company.com/primus-platform:1.0.0
        ports:
        - containerPort: 8080
        - containerPort: 8443
        env:
        - name: JAVA_OPTS
          value: "-Xmx2g -Xms1g"
        - name: PRIMUS_ENV
          value: "production"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
---
apiVersion: v1
kind: Service
metadata:
  name: primus-platform-service
spec:
  selector:
    app: primus-platform
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: LoadBalancer
```

Deploy to K8s:
```bash
kubectl apply -f deployment.yaml
kubectl scale deployment primus-platform --replicas=5
```

## Configuration Management

### Environment Variables

```bash
# Core Configuration
PRIMUS_ENV=production
PRIMUS_PORT=8080
PRIMUS_SECURE_PORT=8443

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=primus
DB_USER=primus
DB_PASSWORD=${DB_PASSWORD}

# Storage Configuration
STORAGE_TYPE=s3
STORAGE_BUCKET=primus-exports
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=${AWS_KEY}
AWS_SECRET_ACCESS_KEY=${AWS_SECRET}

# Security Configuration
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=3600
ENABLE_HTTPS=true
SSL_CERT_PATH=/etc/primus/certs/cert.pem
SSL_KEY_PATH=/etc/primus/certs/key.pem

# Logging Configuration
LOG_LEVEL=INFO
LOG_FILE=/var/log/primus/application.log
ENABLE_AUDIT_LOG=true
```

### Configuration File

```yaml
# application.yml
server:
  port: 8080
  ssl:
    enabled: true
    keyStore: /etc/primus/keystore.jks
    keyStorePassword: ${SSL_PASSWORD}

database:
  url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
  username: ${DB_USER}
  password: ${DB_PASSWORD}
  poolSize: 20

storage:
  default: s3
  backends:
    s3:
      bucket: ${STORAGE_BUCKET}
      region: ${AWS_REGION}
    nas:
      enabled: false

security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}
  oauth2:
    enabled: true
    providers:
      - name: google
        clientId: ${GOOGLE_CLIENT_ID}
        clientSecret: ${GOOGLE_CLIENT_SECRET}

logging:
  level: ${LOG_LEVEL}
  file: ${LOG_FILE}
```

## Monitoring & Health Checks

### Health Check Endpoints

```bash
# Basic health
curl http://localhost:8080/health

# Detailed health
curl http://localhost:8080/health/detailed

# Readiness
curl http://localhost:8080/ready

# Metrics
curl http://localhost:8080/metrics
```

### Key Metrics to Monitor

- Request latency (p50, p95, p99)
- Error rate
- Export processing time
- Storage backend response time
- Database connection pool usage
- JVM heap memory usage
- Thread count

### Logging

```bash
# View logs
tail -f /var/log/primus/application.log

# Filter exports
grep "EXPORT" /var/log/primus/application.log

# View errors
grep "ERROR" /var/log/primus/application.log | tail -20
```

## Scaling Considerations

### Horizontal Scaling

- **Stateless Design**: All instances can handle any request
- **Shared Storage**: All instances access same storage backend
- **Session Management**: Use database/Redis for distributed sessions
- **Load Balancing**: Round-robin or least-connections

### Vertical Scaling

- **Memory**: Increase JVM heap size
- **CPU**: Add more cores
- **Database**: Enable connection pooling, caching
- **Storage**: Optimize I/O, use faster backends

## Backup & Recovery

### Regular Backups

```bash
# Backup database
pg_dump primus > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup exported data
aws s3 sync s3://primus-exports /backup/primus-exports/

# Backup configuration
tar -czf primus-config_$(date +%Y%m%d).tar.gz /etc/primus/
```

### Recovery Procedure

1. Restore database from backup
2. Restore exported data to storage backends
3. Restart services
4. Verify data integrity

## Upgrade Procedure

### Zero-Downtime Upgrade

1. Build new version
2. Start new instances with new code
3. Gradually route traffic to new instances
4. Monitor for errors
5. Shutdown old instances once verified

```bash
# With Kubernetes rolling update
kubectl set image deployment/primus-platform \
  primus=docker.company.com/primus-platform:1.1.0 \
  --record

# Monitor rollout
kubectl rollout status deployment/primus-platform

# Rollback if needed
kubectl rollout undo deployment/primus-platform
```

