package com.primus.config.domain;

import java.time.Instant;

/** A boolean feature flag that can be toggled at runtime without restarting services. */
public final class FeatureFlag {

    private final String name;
    private boolean enabled;
    private final String description;
    private String toggledBy;
    private Instant toggledAt;

    public FeatureFlag(String name, boolean enabled, String description) {
        this.name = name;
        this.enabled = enabled;
        this.description = description;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public String getDescription() { return description; }
    public String getToggledBy() { return toggledBy; }
    public Instant getToggledAt() { return toggledAt; }

    public void enable(String by) { this.enabled = true; this.toggledBy = by; this.toggledAt = Instant.now(); }
    public void disable(String by) { this.enabled = false; this.toggledBy = by; this.toggledAt = Instant.now(); }

    @Override
    public String toString() {
        return "FeatureFlag{name='" + name + "', enabled=" + enabled + '}';
    }
}
