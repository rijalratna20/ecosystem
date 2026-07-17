# primus-nas Module

## Overview

Network-Attached Storage integration service for Primus platform exports.

## Module Location

```
primus-platform/primus-services/primus-nas/
```

## Purpose

Provides NAS backend support for storing and retrieving exported data, including configuration management, connectivity, and file operations.

## Supported NAS Protocols

### SMB/CIFS (Windows Shares)

```yaml
nas:
  protocol: smb
  host: "nas.company.com"
  share: "primus_exports"
  username: "${NAS_USERNAME}"
  password: "${NAS_PASSWORD}"
```

### NFS (Network File System)

```yaml
nas:
  protocol: nfs
  host: "nas.company.com"
  path: "/export/primus"
  mountOptions: "vers=4,proto=tcp"
```

## Configuration

### Basic Configuration

```yaml
storage:
  nas:
    enabled: true
    protocol: smb
    host: nas.company.com
    port: 445
    share: primus_exports
    domain: COMPANY
    username: ${NAS_USERNAME}
    password: ${NAS_PASSWORD}
    
  # Performance tuning
    bufferSize: 65536
    connectionTimeout: 30000
    readTimeout: 60000
    maxConnections: 10
    
  # Storage structure
    basePath: /exports
    retentionDays: 90
```

## Core Components

### NasStorageBackend

```java
public interface NasStorageBackend extends StorageBackend {
    String store(byte[] data, ExportMetadata metadata);
    byte[] retrieve(String storageId);
    void delete(String storageId);
    boolean exists(String storageId);
    long getAvailableSpace();
    List<StorageFile> listFiles(String path);
}
```

### NasConnection

```java
public class NasConnection {
    private String host;
    private int port;
    private String share;
    private String domain;
    private String username;
    private String password;
    
    public void connect();
    public void disconnect();
    public boolean isConnected();
}
```

### File Operations

```java
public class NasFileOperations {
    
    public void uploadFile(String localPath, String remotePath) {
        // Upload file to NAS
    }
    
    public void downloadFile(String remotePath, String localPath) {
        // Download file from NAS
    }
    
    public void deleteFile(String remotePath) {
        // Delete file from NAS
    }
    
    public boolean fileExists(String remotePath) {
        // Check if file exists
    }
    
    public long getFileSize(String remotePath) {
        // Get file size
    }
    
    public List<FileInfo> listDirectory(String remotePath) {
        // List directory contents
    }
}
```

## Storage Directory Structure

```
/nas/exports/
├── 2024/
│   ├── 01/
│   │   ├── 15/
│   │   │   ├── exp_20240115_001/
│   │   │   │   ├── data.json
│   │   │   │   ├── metadata.json
│   │   │   │   └── checksum.txt
│   │   │   └── exp_20240115_002/
│   │   │       └── ...
```

## Export Storage Process

```java
@Service
public class NasExportService {
    
    @Autowired
    private NasStorageBackend nasBackend;
    
    public String storeExport(Export export, byte[] data) {
        // Create directory structure
        String exportPath = createExportPath(export);
        
        // Store data file
        nasBackend.uploadFile(data, exportPath + "/data.json");
        
        // Store metadata
        String metadata = serializeMetadata(export);
        nasBackend.uploadFile(metadata.getBytes(), 
            exportPath + "/metadata.json");
        
        // Verify checksum
        String checksum = calculateChecksum(data);
        nasBackend.uploadFile(checksum.getBytes(), 
            exportPath + "/checksum.txt");
        
        return exportPath;
    }
    
    public byte[] retrieveExport(String exportPath) {
        // Verify checksum first
        String storedChecksum = nasBackend.downloadFile(
            exportPath + "/checksum.txt");
        byte[] data = nasBackend.download(
            exportPath + "/data.json");
        String calculatedChecksum = calculateChecksum(data);
        
        if (!storedChecksum.equals(calculatedChecksum)) {
            throw new DataIntegrityException(
                "Checksum mismatch");
        }
        
        return data;
    }
}
```

## Retention Policy

### Automatic Cleanup

```java
@Service
public class NasRetentionPolicy {
    
    @Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
    public void cleanupExpiredExports() {
        List<Export> expired = exportService
            .findExpiredExports();
        
        for (Export export : expired) {
            String exportPath = getExportPath(export);
            nasBackend.deleteDirectory(exportPath);
            auditLog.log("EXPORT_DELETED", 
                export.getExportId());
        }
    }
}
```

## Error Handling

### Common Errors

```java
public class NasExceptions {
    public class NasConnectionException extends Exception
    public class NasAccessDeniedException extends Exception
    public class NasFileNotFoundException extends Exception
    public class NasSpaceException extends Exception
    public class NasChecksumException extends Exception
}
```

### Error Recovery

```java
@Service
public class NasErrorRecovery {
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    public byte[] retrieveWithRetry(String exportPath) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return nasBackend.retrieve(exportPath);
            } catch (NasConnectionException e) {
                if (attempt == MAX_RETRIES) throw e;
                Thread.sleep(RETRY_DELAY_MS * attempt);
            }
        }
    }
}
```

## Monitoring

### Health Check

```java
@RestController
@RequestMapping("/health/nas")
public class NasHealthCheck {
    
    @GetMapping
    public NasHealthStatus check() {
        return new NasHealthStatus()
            .connected(nasConnection.isConnected())
            .availableSpace(nasBackend.getAvailableSpace())
            .latencyMs(measureLatency());
    }
}
```

### Metrics

- Connection status (up/down)
- Available space on NAS
- Upload/download throughput
- Error rates
- Response times

## Configuration Example

```yaml
spring:
  application:
    name: primus-nas

storage:
  nas:
    enabled: true
    protocol: smb
    host: 192.168.1.100
    port: 445
    share: primus
    domain: COMPANY
    username: ${NAS_USER}
    password: ${NAS_PASSWORD}
    basePath: /primus/exports
    
  retention:
    enabled: true
    daysToKeep: 90
    cleanupSchedule: "0 0 2 * * *"

logging:
  level:
    com.primus.nas: DEBUG
```

## Testing

```bash
./gradlew :primus-platform:primus-services:primus-nas:test
```

## Best Practices

1. **Network Configuration**: Ensure stable NAS network connection
2. **Credentials Management**: Use environment variables, not hardcoded
3. **Backup**: Maintain regular backups of NAS data
4. **Monitoring**: Monitor space and performance
5. **Redundancy**: Consider RAID configuration on NAS
6. **Audit**: Log all NAS operations

