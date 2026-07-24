# shadow-server

Experimental server variant for research and testing.

## Overview

`shadow-server` is an experimental implementation of the Primus server used for:
- Testing alternative architectures
- Performance benchmarking
- Feature prototyping
- Research and development

## ⚠️ Not for Production

This module is **NOT** recommended for production use. Use `primus-server` instead.

## Quick Start

### Build and Run

```bash
cd primus-platform
./gradlew :primus-platform:shadow-server:bootRun
```

### Configuration

```properties
# Same as primus-server, see primus-server/README.md
server.port=8081  # Run on different port than primus-server
```

## Features & Experiments

### Alternative Export Strategy
- Testing stream-based export
- Batch processing vs. streaming comparison
- Memory usage optimization

### Query Optimization
- Alternative SQL query approaches
- Index strategy experiments
- Caching mechanisms

### Storage Experimentation
- Testing multiple storage backends simultaneously
- Failover strategies
- Replication experiments

## Testing

```bash
./gradlew :primus-platform:shadow-server:test
```

## Module Dependencies

Same as `primus-server`

## Comparison Matrix

| Feature | primus-server | shadow-server |
|---------|---------------|---------------|
| Production Ready | ✅ Yes | ❌ No |
| Tested | ✅ Yes | ⚠️ Partial |
| Performance Optimized | ✅ Yes | ⚠️ No |
| Supported | ✅ Yes | ❌ No |

## Research Topics

- [ ] Streaming export optimization
- [ ] Query performance tuning
- [ ] Storage backend comparison
- [ ] Caching strategy evaluation

## Troubleshooting

**Issue**: Behavior different from primus-server
- **Solution**: This is expected. Report findings to architects.

**Issue**: Performance problems
- **Solution**: Document and report. Use primus-server for production.

---

**Module Status**: ⚠️ Experimental (v0.1)  
**Tier**: Research  
**Not Recommended for Production**  
**Last Updated**: July 2026

