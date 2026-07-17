package com.primus.plugin.spi;

/**
 * Marker interface for Primus plugins. Implementations should provide a no-arg constructor
 * and register via the platform's plugin loader (classpath or runtime installer).
 */
public interface Plugin {
    /**
     * Called once when the plugin is initialized by the platform.
     */
    void initialize(PluginContext context);

    /**
     * Called on platform shutdown or plugin unload.
     */
    void shutdown();
}

