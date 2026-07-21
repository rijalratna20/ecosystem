package com.primus.config.service;

import com.primus.config.domain.ConfigProperty;
import com.primus.config.domain.FeatureFlag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** In-memory implementation of {@link ConfigService}. */
public class InMemoryConfigService implements ConfigService {

    private final Map<String, ConfigProperty> properties = new ConcurrentHashMap<>();
    private final Map<String, FeatureFlag> flags = new ConcurrentHashMap<>();

    private static String propertyKey(String key, String env) {
        return env + "::" + key;
    }

    @Override
    public ConfigProperty set(String key, String value, String environment, String updatedBy, String description) {
        String storeKey = propertyKey(key, environment);
        int version = properties.containsKey(storeKey) ? properties.get(storeKey).getVersion() + 1 : 1;
        ConfigProperty prop = new ConfigProperty(key, value, environment, updatedBy, version, description);
        properties.put(storeKey, prop);
        return prop;
    }

    @Override
    public Optional<String> get(String key, String environment) {
        ConfigProperty prop = properties.get(propertyKey(key, environment));
        return prop == null ? Optional.empty() : Optional.of(prop.getValue());
    }

    @Override
    public String get(String key, String environment, String defaultValue) {
        return get(key, environment).orElse(defaultValue);
    }

    @Override
    public List<ConfigProperty> listByEnvironment(String environment) {
        return properties.values().stream()
                .filter(p -> environment.equals(p.getEnvironment()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String key, String environment) {
        return properties.remove(propertyKey(key, environment)) != null;
    }

    @Override
    public FeatureFlag registerFlag(String name, boolean defaultEnabled, String description) {
        FeatureFlag flag = new FeatureFlag(name, defaultEnabled, description);
        flags.put(name, flag);
        return flag;
    }

    @Override
    public void enableFlag(String name, String toggledBy) {
        requireFlag(name).enable(toggledBy);
    }

    @Override
    public void disableFlag(String name, String toggledBy) {
        requireFlag(name).disable(toggledBy);
    }

    @Override
    public boolean isFlagEnabled(String name) {
        FeatureFlag f = flags.get(name);
        return f != null && f.isEnabled();
    }

    @Override
    public List<FeatureFlag> listFlags() {
        return List.copyOf(flags.values());
    }

    private FeatureFlag requireFlag(String name) {
        FeatureFlag f = flags.get(name);
        if (f == null) throw new IllegalArgumentException("Feature flag not found: " + name);
        return f;
    }
}
