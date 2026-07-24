package com.primus.common.constant;

/** Shared API and platform constants. */
public final class PrimusConstants {

    private PrimusConstants() {}

    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;

    public static final String CORRELATION_HEADER = "X-Correlation-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 500;

    public static final long DEFAULT_TIMEOUT_MS = 30_000L;
    public static final int MAX_EXPORT_RETRY_ATTEMPTS = 3;

    public static final String EXPORT_STATUS_REQUESTED  = "REQUESTED";
    public static final String EXPORT_STATUS_RUNNING    = "RUNNING";
    public static final String EXPORT_STATUS_COMPLETED  = "COMPLETED";
    public static final String EXPORT_STATUS_FAILED     = "FAILED";
    public static final String EXPORT_STATUS_CANCELLED  = "CANCELLED";
}
