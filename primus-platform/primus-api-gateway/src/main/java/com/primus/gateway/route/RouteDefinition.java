package com.primus.gateway.route;

/**
 * Descriptor for a single upstream route managed by the API gateway.
 */
public final class RouteDefinition {

    private final String id;
    private final String path;
    private final String upstream;
    private final boolean authRequired;
    private final int maxRequestsPerMinute;

    public RouteDefinition(String id, String path, String upstream,
                           boolean authRequired, int maxRequestsPerMinute) {
        this.id = id;
        this.path = path;
        this.upstream = upstream;
        this.authRequired = authRequired;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    public String getId() { return id; }
    public String getPath() { return path; }
    public String getUpstream() { return upstream; }
    public boolean isAuthRequired() { return authRequired; }
    public int getMaxRequestsPerMinute() { return maxRequestsPerMinute; }

    @Override
    public String toString() {
        return "RouteDefinition{id='" + id + "', path='" + path + "', upstream='" + upstream + "'}";
    }
}
