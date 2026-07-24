# primus-nas

NAS (Network Attached Storage) backend adapter.

## Overview

`primus-nas` implements storage backend using NAS/SMB/NFS filesystems. It handles reading and writing export data to network storage devices.

## Quick Start

### Configuration

```properties
# NAS Configuration
primus.storage.nas.enabled=true
primus.storage.nas.type=smb  # smb or nfs
primus.storage.nas.server=nas.example.com
primus.storage.nas.share=/exports
primus.storage.nas.username=primus_user
primus.storage.nas.password=primus_password
primus.storage.nas.mount-point=/mnt/primus
```

### SMB/CIFS Setup

```bash
# Install CIFS utilities
sudo apt-get install cifs-utils

# Mount NAS share
sudo mount -t cifs //nas.example.com/exports /mnt/primus \
  -o username=primus_user,password=primus_password,uid=1000,gid=1000
```

### NFS Setup

```bash
# Install NFS utilities
sudo apt-get install nfs-common

# Mount NAS share
sudo mount -t nfs nas.example.com:/exports /mnt/primus
```

## Storage Structure

```
/mnt/primus/
├── CustomerService/
│   ├── exp_001/
│   │   ├── customers/
│   │   │   ├── 12345.json
│   │   │   ├── 12346.json
│   │   ├── accounts/
│   │   │   ├── acc_001.json
│   ├── exp_002/
├── LoanService/
│   ├── exp_001/
```

## APIs

### StorageProvider Interface

```java
void put(String path, InputStream data)
InputStream get(String path)
boolean exists(String path)
void delete(String path)
List<String> list(String directory)
long getSize(String path)
```

## Configuration

```properties
# Connection
primus.storage.nas.connection-timeout=30s
primus.storage.nas.read-timeout=60s

# Buffering
primus.storage.nas.buffer-size=8192

# Retry
primus.storage.nas.max-retries=3
primus.storage.nas.retry-delay-ms=1000

# Performance
primus.storage.nas.thread-pool-size=10
primus.storage.nas.max-concurrent-uploads=5
```

## Testing

```bash
./gradlew :primus-platform:primus-nas:test
```

## Module Dependencies

- `primus-common`

## Performance Considerations

### Throughput
- Single connection: ~50 MB/s
- With 5 concurrent connections: ~200 MB/s

### Latency
- Write latency: ~100-200ms for small files
- Read latency: ~50-100ms for cached reads

### Capacity
- Typical NAS: 10-100 TB
- Daily backup recommendation: 1% of capacity

## Troubleshooting

**Issue**: "Cannot mount NAS share"
- **Solution**: Check NAS server is running, verify credentials and firewall rules

**Issue**: "Permission denied" writing to share
- **Solution**: Verify user has write permissions on NAS share

**Issue**: "Slow performance"
- **Solution**: Check network bandwidth, increase thread pool size, verify NAS health

**Issue**: "Connection timeout"
- **Solution**: Increase connection timeout in configuration, check network connectivity

## Limitations

⚠️ **Single-Region**: NAS-only storage doesn't support multi-region deployments  
⚠️ **Not Cloud-Native**: Requires local mounting, not suitable for Kubernetes  
⚠️ **Scalability**: Limited by single NAS capacity and bandwidth  

**For production deployments, migrate to `primus-storage-s3` (Tier 3)**

## Migration

See `primus-storage-s3` for cloud-based storage with better scalability and reliability.

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Storage Adapter  
**Last Updated**: July 2026

