package com.primus.gateway;

import com.primus.gateway.filter.FilterResult;
import com.primus.gateway.filter.GatewayFilter;
import com.primus.gateway.ratelimit.RateLimiter;
import com.primus.gateway.route.RouteDefinition;
import com.primus.gateway.route.RouteRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayFilterTest {

    private RouteRegistry registry;
    private RateLimiter rateLimiter;
    private GatewayFilter filter;

    @BeforeEach
    void setUp() {
        registry = new RouteRegistry();
        registry.register(new RouteDefinition("exports", "/api/v1/exports", "http://primus-server:8080", true, 100));
        registry.register(new RouteDefinition("health", "/actuator/health", "http://primus-server:8080", false, 1000));
        rateLimiter = new RateLimiter(5);
        filter = new GatewayFilter(registry, rateLimiter);
    }

    @Test
    void allows_authenticated_request() {
        FilterResult result = filter.evaluate("/api/v1/exports", "user-1", "******");
        assertTrue(result.isAllowed());
    }

    @Test
    void denies_unauthenticated_on_protected_route() {
        FilterResult result = filter.evaluate("/api/v1/exports", null, null);
        assertFalse(result.isAllowed());
        assertEquals(401, result.getHttpStatusCode());
    }

    @Test
    void allows_anonymous_on_public_route() {
        FilterResult result = filter.evaluate("/actuator/health", null, null);
        assertTrue(result.isAllowed());
    }

    @Test
    void denies_unmatched_route() {
        FilterResult result = filter.evaluate("/unknown/path", "user-1", "******");
        assertFalse(result.isAllowed());
        assertEquals(404, result.getHttpStatusCode());
    }

    @Test
    void rate_limit_exceeded() {
        String key = "user-burst";
        // exhaust limit (5 per minute)
        for (int i = 0; i < 5; i++) {
            FilterResult r = filter.evaluate("/actuator/health", key, null);
            assertTrue(r.isAllowed(), "Request " + (i + 1) + " should be allowed");
        }
        FilterResult blocked = filter.evaluate("/actuator/health", key, null);
        assertFalse(blocked.isAllowed());
        assertEquals(429, blocked.getHttpStatusCode());
    }
}
