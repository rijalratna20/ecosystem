package com.primus.config;

import com.primus.config.service.ConfigService;
import com.primus.config.service.InMemoryConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConfigServiceTest {

    private ConfigService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryConfigService();
    }

    @Test
    void set_and_get_property() {
        service.set("db.url", "jdbc:postgresql://localhost/primus", "prod", "admin", "DB url");
        Optional<String> val = service.get("db.url", "prod");
        assertTrue(val.isPresent());
        assertEquals("jdbc:postgresql://localhost/primus", val.get());
    }

    @Test
    void get_missing_returns_default() {
        String val = service.get("missing.key", "dev", "default-value");
        assertEquals("default-value", val);
    }

    @Test
    void version_increments_on_update() {
        service.set("key", "v1", "prod", "admin", null);
        var p2 = service.set("key", "v2", "prod", "admin", null);
        assertEquals(2, p2.getVersion());
        assertEquals("v2", service.get("key", "prod").orElseThrow());
    }

    @Test
    void environments_are_isolated() {
        service.set("flag", "prod-value", "prod", "admin", null);
        service.set("flag", "dev-value", "dev", "admin", null);
        assertEquals("prod-value", service.get("flag", "prod").orElseThrow());
        assertEquals("dev-value", service.get("flag", "dev").orElseThrow());
    }

    @Test
    void delete_removes_property() {
        service.set("temp", "x", "prod", "admin", null);
        assertTrue(service.delete("temp", "prod"));
        assertFalse(service.get("temp", "prod").isPresent());
    }

    @Test
    void feature_flag_toggle() {
        service.registerFlag("new-export-ui", false, "New Angular UI experiment");
        assertFalse(service.isFlagEnabled("new-export-ui"));
        service.enableFlag("new-export-ui", "eng-lead");
        assertTrue(service.isFlagEnabled("new-export-ui"));
        service.disableFlag("new-export-ui", "eng-lead");
        assertFalse(service.isFlagEnabled("new-export-ui"));
    }

    @Test
    void missing_flag_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.enableFlag("nonexistent", "admin"));
    }
}
