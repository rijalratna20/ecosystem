# Retrieval Flow

## Overview

The retrieval flow handles fetching previously exported data from storage systems with validation, authorization, and transformation.

## Retrieval Process

### Step 1: Request Data Retrieval

```http
GET /api/v1/exports/{exportId}/retrieve HTTP/1.1
Authorization: Bearer {token}

Query Parameters:
- format: json|csv|parquet
- fields: comma-separated field names (optional)
- filter: query filter (optional)
```

### Step 2: Authorization Check

- Verify user authentication
- Check if user owns/has access to export
- Verify role-based permissions
- Check export not expired

### Step 3: Locate Data

- Query storage metadata
- Determine storage backend location
- Verify data integrity via checksum

### Step 4: Retrieve Data

```json
{
  "exportId": "exp_20240115_001",
  "retrievalStartTime": "2024-01-15T10:45:00Z",
  "recordCount": 10000,
  "format": "JSON",
  "status": "RETRIEVING"
}
```

### Step 5: Transform Data

- Deserialize from storage format
- Apply field selections if specified
- Apply retrieval filters
- Format for response

### Step 6: Return Data

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Disposition: attachment; filename="export_20240115_001.json"

[
  {
    "userId": "user_001",
    "email": "user@example.com",
    "createdDate": "2023-06-15"
  },
  ...
]
```

## Retrieval Methods

### 1. Direct API Response

**Best for**: Small exports, interactive access
```http
GET /api/v1/exports/{exportId}/retrieve
```

### 2. Paginated Retrieval

**Best for**: Large exports, memory-constrained clients
```http
GET /api/v1/exports/{exportId}/retrieve?page=1&pageSize=1000

Response:
{
  "page": 1,
  "pageSize": 1000,
  "totalPages": 10,
  "totalRecords": 10000,
  "records": [...]
}
```

### 3. Streaming Retrieval

**Best for**: Very large exports, reliable connections
```http
GET /api/v1/exports/{exportId}/retrieve?stream=true

Response: Server-Sent Events (SSE)
```

### 4. Download Link

**Best for**: Browser downloads, external integrations
```http
POST /api/v1/exports/{exportId}/download-link HTTP/1.1

Response:
{
  "downloadUrl": "https://api.primus.com/downloads/dl_xxxxx",
  "expiresIn": 3600,
  "expiresAt": "2024-01-15T11:45:00Z"
}
```

## Filtering During Retrieval

### Filter Syntax

```
GET /api/v1/exports/{exportId}/retrieve?filter=status:ACTIVE,createdAfter:2024-01-01

Supported Operators:
- eq: exact match
- ne: not equal
- gt: greater than
- lt: less than
- in: value in list
- like: pattern match
```

### Filter Examples

```
Simple: email:user@example.com
Range: createdDate:gt:2024-01-01,lt:2024-12-31
Multiple: status:ACTIVE,tier:in:PREMIUM|ENTERPRISE
Pattern: email:like:*@company.com
```

## Caching Strategy

### Cache Levels

1. **Application Cache**: Redis, in-memory cache
2. **Storage Cache**: Frequently accessed exports
3. **CDN Cache**: Global distribution

### Cache Settings

```yaml
retrieval:
  cache:
    enabled: true
    ttl: 3600
    maxSize: 1GB
    strategy: LRU
```

## Performance Optimizations

- **Compression**: Gzip, Brotli for response compression
- **Partial Retrieval**: Fetch only needed fields
- **Parallel Downloads**: Multi-part downloads
- **Connection Pooling**: Reuse storage connections
- **Query Optimization**: Index frequently filtered fields

## Error Handling

### Common Errors

- **EXPORT_NOT_FOUND**: Export ID doesn't exist
- **EXPORT_EXPIRED**: Export has expired
- **ACCESS_DENIED**: User lacks permission
- **STORAGE_ERROR**: Can't read from storage
- **FORMAT_ERROR**: Unsupported format requested

### Error Response

```json
{
  "error": {
    "code": "EXPORT_EXPIRED",
    "message": "Export expired on 2024-04-15",
    "exportId": "exp_20240115_001"
  }
}
```

