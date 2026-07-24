package com.primus.gateway.filter;

import com.primus.gateway.ratelimit.RateLimiter;
import com.primus.gateway.route.RouteDefinition;
import com.primus.gateway.route.RouteRegistry;

import java.util.Optional;

/**
 * Entry-point filter that evaluates rate-limiting and authentication
 * for every inbound request before it is forwarded to an upstream service.
 */
public class GatewayFilter {

    private final RouteRegistry routeRegistry;
    private final RateLimiter rateLimiter;

    public GatewayFilter(RouteRegistry routeRegistry, RateLimiter rateLimiter) {
        this.routeRegistry = routeRegistry;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Evaluate the filter chain for an inbound request.
     *
     * @param path        the request path (e.g. "/api/v1/exports")
     * @param principalId the authenticated principal, or null for anonymous requests
     * @param bearerToken the JWT bearer token, or null if absent
     * @return filter result; call {@link FilterResult#isAllowed()} to decide whether to forward
     */
    public FilterResult evaluate(String path, String principalId, String bearerToken) {
        Optional<RouteDefinition> route = routeRegistry.match(path);
        if (route.isEmpty()) {
            return FilterResult.deny(404, "No route matched: " + path);
        }

        RouteDefinition r = route.get();

        if (r.isAuthRequired() && (bearerToken == null || bearerToken.isBlank())) {
            return FilterResult.deny(401, "Authentication required for route: " + r.getId());
        }

        String rateLimitKey = principalId != null ? principalId : path;
        if (!rateLimiter.isAllowed(rateLimitKey)) {
            return FilterResult.deny(429, "Rate limit exceeded for: " + rateLimitKey);
        }

        return FilterResult.allow();
    }
}
