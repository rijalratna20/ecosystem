# primus-search

Search and indexing shim for Elasticsearch/OpenSearch.

## Overview

`primus-search` provides a search abstraction layer that allows applications to search exported data using Elasticsearch or OpenSearch backends.

## Quick Start

### Configuration

```properties
# Elasticsearch Configuration
primus.search.backend=elasticsearch
primus.search.elasticsearch.hosts=localhost:9200
primus.search.elasticsearch.username=elastic
primus.search.elasticsearch.password=your_password
primus.search.elasticsearch.index-prefix=primus
```

### Search Query

```java
SearchClient searchClient = SearchClient.builder()
    .application("CustomerService")
    .build();

SearchRequest request = SearchRequest.builder()
    .query("customerId:12345")
    .filters(Arrays.asList(
        new Filter("status", "ACTIVE"),
        new Filter("region", "US")
    ))
    .pageSize(50)
    .pageNumber(1)
    .build();

SearchResponse response = searchClient.search(request);
List<SearchResult> results = response.getResults();
```

## Key Features

### Full-Text Search
```java
searchClient.search("SELECT * WHERE name matches 'john*'");
```

### Faceted Search
```java
SearchRequest request = SearchRequest.builder()
    .query("status:ACTIVE")
    .facets(Arrays.asList("region", "status", "type"))
    .build();

SearchResponse response = searchClient.search(request);
Map<String, List<Facet>> facets = response.getFacets();
```

### Filtering & Sorting
```java
SearchRequest request = SearchRequest.builder()
    .query("*:*")
    .filters(Arrays.asList(
        new Filter("createdAt", ">", "2026-01-01"),
        new Filter("amount", "<", "10000")
    ))
    .sort(new Sort("createdAt", Sort.Direction.DESC))
    .build();
```

## APIs

### SearchClient

```java
SearchResponse search(SearchRequest request)
void index(String id, Map<String, Object> document)
void delete(String id)
void bulkIndex(List<Map<String, Object>> documents)
long count(String query)
```

### Query Syntax

```
# Simple query
customerId:12345

# Range query
createdAt:[2026-01-01 TO 2026-12-31]

# Boolean operators
status:ACTIVE AND region:US
type:(CUSTOMER OR PROSPECT)

# Wildcards
name:john*
email:*@example.com
```

## Configuration

```properties
# Connection
primus.search.elasticsearch.max-retries=3
primus.search.elasticsearch.request-timeout=30s
primus.search.elasticsearch.socket-timeout=60s

# Indexing
primus.search.indexing.batch-size=1000
primus.search.indexing.thread-pool-size=4
primus.search.indexing.auto-refresh=true

# Performance
primus.search.cache.enabled=true
primus.search.cache.ttl-seconds=300
```

## Testing

```bash
./gradlew :primus-platform:primus-search:test
```

## Module Dependencies

- `primus-common`

## Index Management

### Create Index

```java
IndexManager indexManager = IndexManager.getInstance();
indexManager.createIndex("primus-customersservice-2026-07");
```

### Delete Index

```java
indexManager.deleteIndex("primus-customersservice-2026-06");
```

### Reindex

```java
indexManager.reindex("primus-customersservice-2026-06", "primus-customersservice-2026-07");
```

## Performance Tuning

### Typical Metrics
- Query latency (p50): 10-50ms
- Query latency (p95): 50-200ms
- Index size: ~1GB per 1M documents

### Optimization Tips
1. Use appropriate filters to reduce result set
2. Avoid wildcards at the beginning of queries
3. Use pagination for large results
4. Archive old indices regularly
5. Monitor heap usage and adjust JVM settings

## Troubleshooting

**Issue**: "Connection refused to Elasticsearch"
- **Solution**: Verify Elasticsearch is running on correct host and port

**Issue**: "Index not found"
- **Solution**: Create index first with `createIndex()` method

**Issue**: "Slow query performance"
- **Solution**: Add appropriate filters, check Elasticsearch health, monitor resource usage

**Issue**: "Out of memory errors"
- **Solution**: Increase JVM heap size, reduce query result size, archive old indices

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Search Adapter  
**Last Updated**: July 2026

