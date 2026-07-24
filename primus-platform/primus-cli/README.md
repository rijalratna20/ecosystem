# primus-cli

Command-line interface for operators and administrators.

## Overview

`primus-cli` provides powerful command-line tools for operators, DevOps engineers, and administrators to manage Primus platform operations without needing the web UI.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 8 - Phase 2)**

**Target Launch**: Week 20-24 of Phase 2  
**Phase**: v1.1 (Production Hardening) delivery  
**Priority**: MEDIUM - Enables operator automation  

## Purpose

CLI operations:
- **Export/retrieval**: Trigger exports and retrieve data
- **Application management**: Register/update applications
- **Audit queries**: Search audit logs
- **Health checks**: Verify platform health
- **Automation**: Script platform operations

## Command Reference

### Export Commands

```bash
# Create export
primus export --app CustomerService \
  --collections customers,accounts \
  --filter "region='US'" \
  --wait

# List exports
primus exports list \
  --app CustomerService \
  --status completed \
  --since 2026-07-01 \
  --format json

# Get export status
primus export status exp_123456

# Download export
primus export download exp_123456 -o data.json
```

### Retrieval Commands

```bash
# Retrieve data
primus retrieve exp_123456 \
  --collection customers \
  --id 12345

# List available exports for retrieval
primus exports available --app CustomerService
```

### Application Commands

```bash
# Register application
primus app register --file metadata.yaml

# List applications
primus apps list

# Get application metadata
primus app metadata CustomerService

# Update application
primus app update CustomerService --file new-metadata.yaml
```

### Audit Commands

```bash
# Search audit logs
primus audit search \
  --action EXPORT_INITIATED \
  --user user_123 \
  --from 2026-07-01 \
  --to 2026-07-31 \
  --format table

# Generate compliance report
primus audit report \
  --type SOX \
  --from 2026-07-01 \
  --to 2026-07-31 \
  -o report.csv

# View sensitive data access
primus audit sensitive-access \
  --field SSN \
  --last-days 30
```

### Approval Commands

```bash
# List pending approvals
primus approvals pending --approver user_123

# Approve export
primus approval approve app_req_123 \
  --reason "Approved for UAT" \
  --expires 7d

# Reject approval
primus approval reject app_req_123 \
  --reason "Insufficient justification"
```

### Health Commands

```bash
# Check platform status
primus health

# Check service status
primus service status primus-server
primus service status primus-storage-s3
primus service status primus-audit

# Run health check
primus health check --verbose
```

### Configuration Commands

```bash
# Get configuration
primus config get primus.rate-limit.per-minute

# Set configuration
primus config set primus.rate-limit.per-minute 100

# List all configuration
primus config list --env prod

# Watch configuration changes
primus config watch primus.*
```

## Configuration

```bash
# Initialize CLI
primus setup \
  --server https://primus.example.com \
  --api-key sk_live_xyz123 \
  --profile prod

# Use specific profile
primus --profile prod exports list

# Set environment variables
export PRIMUS_SERVER=https://primus.example.com
export PRIMUS_API_KEY=sk_live_xyz123
export PRIMUS_PROFILE=prod
```

## Output Formats

```bash
# Table format (default)
primus exports list --format table

# JSON format (for scripting)
primus exports list --format json

# CSV format (for spreadsheets)
primus exports list --format csv

# YAML format
primus exports list --format yaml
```

## Scripting Examples

### Daily Export Script
```bash
#!/bin/bash

TIMESTAMP=$(date +%Y-%m-%d)
APP="CustomerService"

# Create export
EXPORT_ID=$(primus export --app $APP \
  --collections customers,accounts \
  --filter "modifiedDate >= '${TIMESTAMP}'" \
  --wait \
  --format json | jq -r '.exportId')

# Download
primus export download $EXPORT_ID -o "exports/${APP}-${TIMESTAMP}.json"

# Report
echo "Export $EXPORT_ID completed successfully" | \
  mail -s "Daily export complete" ops@example.com
```

### Monitoring Script
```bash
#!/bin/bash

# Check health every 5 minutes
while true; do
  STATUS=$(primus health --format json)
  if ! echo $STATUS | jq -e '.status == "UP"'; then
    echo "ALERT: Platform unhealthy!"
    # Send alert
  fi
  sleep 300
done
```

## Installation

### Binary Distribution
```bash
# Download binary
curl -O https://releases.primus.io/primus-cli-1.0.0-darwin-amd64

# Make executable
chmod +x primus-cli-1.0.0-darwin-amd64

# Install
sudo mv primus-cli-1.0.0-darwin-amd64 /usr/local/bin/primus

# Verify
primus --version
```

### Package Manager
```bash
# macOS
brew install primus-cli

# Ubuntu
sudo apt install primus-cli

# CentOS
sudo yum install primus-cli
```

## Authentication

```bash
# Using API key
primus --api-key sk_live_xyz123 exports list

# Using JWT token
primus --token "eyJ0eXAiOiJKV1QiLCJhbGc..." exports list

# Interactive login
primus login
# Generates OAuth2 flow

# Profile-based (recommended)
primus config set profile=prod
primus exports list
```

## Exit Codes

```
0  — Success
1  — General error
2  — Invalid command
3  — API error
4  — Authentication error
5  — Connection error
```

## Bash Completion

```bash
# Enable completion
eval "$(primus completion bash)"

# Then use:
primus exp<TAB>  →  primus export
primus --<TAB>   →  Shows all flags
```

## Module Dependencies

- `primus-sdk` (Java SDK)
- Python client library (for cross-language support)

## Testing Strategy

### Unit Tests
- Command parsing
- Output formatting
- Error handling

### Integration Tests
- End-to-end CLI workflows
- API integration
- Authentication

## Deployment

CLI is distributed as:
- Binary executable
- Package (Homebrew, apt, yum)
- Docker image
- Source code

## Documentation

- `primus help` — Show help
- `primus COMMAND help` — Command-specific help
- `primus COMMAND --help` — Flags help
- Online docs: https://docs.primus.io/cli

## Related Modules

- **primus-sdk** (Foundation) — Underlying API calls
- **primus-server** (Core) — Backend APIs

---

**Tier**: 8 (Operations - Phase 2)  
**Status**: 🟠 NOT STARTED  
**Priority**: MEDIUM  
**Target Start**: October 8, 2026  
**Target Completion**: October 22, 2026

