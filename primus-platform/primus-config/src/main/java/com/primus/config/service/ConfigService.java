package com.primus.config.service;

import com.primus.config.domain.ConfigProperty;
import com.primus.config.domain.FeatureFlag;

import java.util.List;
import java.util.Optional;

/**
 * Central configuration service with hot-reload support and feature flags.
 *
 * <p>Implementations may back this with a database, Consul, Vault, or AWS SSM Parameter Store.
 */
public interface ConfigService {

    /** Store or update a property. */
    ConfigProperty set(String key, String value, String environment, String updatedBy, String description);

    /** Retrieve a property value, or empty if not found. */
    Optional<String> get(String key, String environment);

    /** Retrieve a property value with a default fallback. */
    String get(String key, String environment, String defaultValue);

    /** List all properties for an environment. */
    List<ConfigProperty> listByEnvironment(String environment);

    /** Delete a property. Returns true if it existed. */
    boolean delete(String key, String environment);

    // --- Feature Flags ---

    /** Register a new feature flag. */
    FeatureFlag registerFlag(String name, boolean defaultEnabled, String description);

    /** Enable a feature flag. */
    void enableFlag(String name, String toggledBy);

    /** Disable a feature flag. */
    void disableFlag(String name, String toggledBy);

    /** Check whether a feature flag is currently enabled. */
    boolean isFlagEnabled(String name);

    /** List all feature flags. */
    List<FeatureFlag> listFlags();
}
