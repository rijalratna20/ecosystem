package com.primus.plugin.spi;

import java.util.Map;

/**
 * Minimal context provided to plugins on initialization. Can be extended with
 * configuration, logger, service locators, etc.
 */
public interface PluginContext {
    Map<String, String> getConfig();
}

