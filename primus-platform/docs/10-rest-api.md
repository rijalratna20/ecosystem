# REST API Reference

## Base URL

```
https://api.primus.com/api/v1
wss://api.primus.com/ws/v1  # WebSocket endpoint
```

## Authentication

All requests require `Authorization` header:

```
Authorization: Bearer {token}
```

## Common Response Format

### Success Response

```json
{
  "status": "SUCCESS",
  "code": 200,
  "data": { ... },
  "metadata": {
    "timestamp": "2024-01-15T10:30:45Z",
    "requestId": "req_xxxxx"
  }
}
```

### Error Response

```json
{
  "status": "ERROR",
  "code": 400,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Validation failed",
    "details": { ... }
  },
  "metadata": {
    "timestamp": "2024-01-15T10:30:45Z",
    "requestId": "req_xxxxx"
  }
}
```

## Export Endpoints

### Create Export

```http
POST /exports
Content-Type: application/json

{
  "contractName": "UserProfile",
  "contractVersion": "1.0",
  "filters": { },
  "format": "JSON",
  "destination": "nas"
}

Response: 201 Created
{
  "exportId": "exp_20240115_001",
  "status": "PROCESSING",
  "createdAt": "2024-01-15T10:30:45Z"
}
```

### List Exports

```http
GET /exports?status=COMPLETED&limit=10&offset=0

Response: 200 OK
{
  "exports": [
    {
      "exportId": "exp_20240115_001",
      "contractName": "UserProfile",
      "status": "COMPLETED",
      "recordCount": 10000,
      "fileSize": "52.4 MB",
      "createdAt": "2024-01-15T10:30:45Z",
      "completedAt": "2024-01-15T10:35:45Z",
      "expiresAt": "2024-04-15T10:30:45Z"
    }
  ],
  "pagination": {
    "total": 150,
    "limit": 10,
    "offset": 0
  }
}
```

### Get Export Details

```http
GET /exports/{exportId}

Response: 200 OK
{
  "exportId": "exp_20240115_001",
  "contractName": "UserProfile",
  "contractVersion": "1.0",
  "status": "COMPLETED",
  "recordCount": 10000,
  "recordsFailed": 0,
  "fileSize": "52.4 MB",
  "checksum": "abc123def456...",
  "createdAt": "2024-01-15T10:30:45Z",
  "completedAt": "2024-01-15T10:35:45Z",
  "expiresAt": "2024-04-15T10:30:45Z",
  "createdBy": "user@company.com",
  "errors": []
}
```

### Get Export Status

```http
GET /exports/{exportId}/status

Response: 200 OK
{
  "exportId": "exp_20240115_001",
  "status": "PROCESSING",
  "progress": 45,
  "recordsProcessed": 4500,
  "recordsTotal": 10000,
  "estimatedTimeRemaining": 300
}
```

### Retrieve Export Data

```http
GET /exports/{exportId}/retrieve?format=JSON&page=1&pageSize=100

Response: 200 OK
Content-Type: application/json

[
  {
    "userId": "user_001",
    "email": "user@example.com",
    "createdDate": "2023-06-15"
  }
]
```

### Delete Export

```http
DELETE /exports/{exportId}

Response: 204 No Content
```

## Contract Endpoints

### List Contracts

```http
GET /contracts?namespace=com.primus.user

Response: 200 OK
{
  "contracts": [
    {
      "name": "UserProfile",
      "latestVersion": "1.0",
      "namespace": "com.primus.user",
      "description": "User profile contract",
      "createdAt": "2024-01-01T00:00:00Z",
      "versions": ["1.0", "0.9"]
    }
  ]
}
```

### Get Contract Definition

```http
GET /contracts/{contractName}/versions/{version}

Response: 200 OK
{
  "name": "UserProfile",
  "version": "1.0",
  "namespace": "com.primus.user",
  "fields": [ ... ],
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### Create/Update Contract

```http
POST /contracts
Content-Type: application/yaml

name: NewContract
version: "1.0"
namespace: "com.primus.new"
fields:
  - name: id
    type: UUID
    required: true

Response: 201 Created
{
  "contractId": "contract_xxxxx",
  "name": "NewContract",
  "version": "1.0"
}
```

## Data Endpoints

### Query Data

```http
POST /data/query
Content-Type: application/json

{
  "contractName": "UserProfile",
  "filters": {
    "status": "ACTIVE",
    "createdAfter": "2024-01-01"
  },
  "limit": 100,
  "offset": 0
}

Response: 200 OK
{
  "records": [ ... ],
  "total": 1000,
  "limit": 100,
  "offset": 0
}
```

### Create Record

```http
POST /data/records
Content-Type: application/json

{
  "contractName": "UserProfile",
  "data": {
    "userId": "user_new",
    "email": "new@example.com",
    "status": "ACTIVE"
  }
}

Response: 201 Created
{
  "recordId": "rec_xxxxx",
  "contractName": "UserProfile",
  "data": { ... }
}
```

### Update Record

```http
PUT /data/records/{recordId}
Content-Type: application/json

{
  "email": "updated@example.com",
  "status": "INACTIVE"
}

Response: 200 OK
{
  "recordId": "rec_xxxxx",
  "data": { ... }
}
```

### Delete Record

```http
DELETE /data/records/{recordId}

Response: 204 No Content
```

## WebSocket Events

### Subscribe to Export Status

```
wss://api.primus.com/ws/v1/exports/{exportId}

Message Format:
{
  "event": "STATUS_UPDATED",
  "data": {
    "status": "PROCESSING",
    "progress": 45
  }
}
```

## HTTP Status Codes

- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `204 No Content`: Successful deletion
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Missing/invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error

## Rate Limiting

- **Limit**: 1000 requests per minute
- **Headers**: 
  - `X-RateLimit-Limit`: Maximum requests
  - `X-RateLimit-Remaining`: Requests remaining
  - `X-RateLimit-Reset`: Time until reset (Unix timestamp)

