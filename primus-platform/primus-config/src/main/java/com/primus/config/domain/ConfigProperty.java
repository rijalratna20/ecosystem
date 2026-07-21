package com.primus.config.domain;

import java.time.Instant;

/**
 * A named configuration property with versioning and environment scoping.
 */
public final class ConfigProperty {

    private final String key;
    private final String value;
    private final String environment;
    private final String updatedBy;
    private final Instant updatedAt;
    private final int version;
    private final String description;

    public ConfigProperty(String key, String value, String environment,
                          String updatedBy, int version, String description) {
        this.key = key;
        this.value = value;
        this.environment = environment;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        this.version = version;
        this.description = description;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public String getEnvironment() { return environment; }
    public String getUpdatedBy() { return updatedBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public int getVersion() { return version; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "ConfigProperty{key='" + key + "', env='" + environment + "', version=" + version + '}';
    }
}
