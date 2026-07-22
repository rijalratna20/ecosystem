package com.primus.metadata.registry;

import com.primus.metadata.model.ApplicationMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of application metadata. Thread-safe.
 * In production this would be backed by a persistent store.
 */
public class MetadataRegistry {

    private final Map<String, ApplicationMetadata> registry = new ConcurrentHashMap<>();

    /**
     * Register or replace the metadata for an application.
     */
    public void register(ApplicationMetadata metadata) {
        if (metadata == null || metadata.getAppId() == null) {
            throw new IllegalArgumentException("metadata and appId must not be null");
        }
        registry.put(metadata.getAppId(), metadata);
    }

    public Optional<ApplicationMetadata> find(String appId) {
        return Optional.ofNullable(registry.get(appId));
    }

    public ApplicationMetadata get(String appId) {
        ApplicationMetadata meta = registry.get(appId);
        if (meta == null) {
            throw new IllegalArgumentException("No metadata registered for appId: " + appId);
        }
        return meta;
    }

    public boolean contains(String appId) {
        return registry.containsKey(appId);
    }

    public Collection<ApplicationMetadata> all() {
        return registry.values();
    }

    public void deregister(String appId) {
        registry.remove(appId);
    }

    public int size() {
        return registry.size();
    }
}
