package com.primus.common.logging;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Thread-local correlation context propagated via MDC for structured log correlation.
 */
public final class CorrelationContext {

    public static final String CORRELATION_ID_KEY = "correlationId";
    public static final String USER_KEY = "userId";
    public static final String APP_KEY = "appId";

    private CorrelationContext() {}

    /** Set a correlation ID, generating one if the provided value is null. */
    public static String set(String correlationId) {
        String id = (correlationId != null && !correlationId.isBlank())
                ? correlationId
                : UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, id);
        return id;
    }

    public static String get() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    public static void setUser(String userId) {
        if (userId != null) MDC.put(USER_KEY, userId);
    }

    public static void setApp(String appId) {
        if (appId != null) MDC.put(APP_KEY, appId);
    }

    /** Clear all Primus context keys from MDC. Should be called in finally blocks. */
    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
        MDC.remove(USER_KEY);
        MDC.remove(APP_KEY);
    }

    /** Generate a new correlation ID and store it. */
    public static String generate() {
        return set(null);
    }
}
