package com.primus.gateway.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * In-memory registry of all registered routes.
 * In production, routes can be loaded from a database or configuration server.
 */
public class RouteRegistry {

    private final List<RouteDefinition> routes = new ArrayList<>();

    /** Register a new route. */
    public synchronized void register(RouteDefinition route) {
        if (route == null) throw new IllegalArgumentException("route must not be null");
        routes.add(route);
    }

    /** Find the best-matching route for the given request path. */
    public Optional<RouteDefinition> match(String requestPath) {
        return routes.stream()
                .filter(r -> requestPath != null && requestPath.startsWith(r.getPath()))
                .findFirst();
    }

    /** Return an immutable snapshot of all registered routes. */
    public List<RouteDefinition> all() {
        return Collections.unmodifiableList(routes);
    }

    /** Remove a route by id. */
    public synchronized boolean deregister(String routeId) {
        return routes.removeIf(r -> r.getId().equals(routeId));
    }
}
