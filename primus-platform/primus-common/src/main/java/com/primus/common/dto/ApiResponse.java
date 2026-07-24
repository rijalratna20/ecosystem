package com.primus.common.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API response envelope used across all Primus REST endpoints.
 */
public class ApiResponse<T> {

    private final String status;
    private final int code;
    private final T data;
    private final ErrorInfo error;
    private final ResponseMetadata metadata;

    private ApiResponse(Builder<T> builder) {
        this.status = builder.status;
        this.code = builder.code;
        this.data = builder.data;
        this.error = builder.error;
        this.metadata = builder.metadata;
    }

    public String getStatus() { return status; }
    public int getCode() { return code; }
    public T getData() { return data; }
    public ErrorInfo getError() { return error; }
    public ResponseMetadata getMetadata() { return metadata; }

    public boolean isSuccess() { return "SUCCESS".equals(status); }

    public static <T> ApiResponse<T> ok(T data) {
        return new Builder<T>()
                .status("SUCCESS")
                .code(200)
                .data(data)
                .metadata(ResponseMetadata.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return new Builder<T>()
                .status("SUCCESS")
                .code(201)
                .data(data)
                .metadata(ResponseMetadata.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, int httpCode) {
        return new Builder<T>()
                .status("ERROR")
                .code(httpCode)
                .error(new ErrorInfo(errorCode, message, null))
                .metadata(ResponseMetadata.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, int httpCode,
                                            Map<String, Object> details) {
        return new Builder<T>()
                .status("ERROR")
                .code(httpCode)
                .error(new ErrorInfo(errorCode, message, details))
                .metadata(ResponseMetadata.now())
                .build();
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private String status;
        private int code;
        private T data;
        private ErrorInfo error;
        private ResponseMetadata metadata;

        public Builder<T> status(String status) { this.status = status; return this; }
        public Builder<T> code(int code) { this.code = code; return this; }
        public Builder<T> data(T data) { this.data = data; return this; }
        public Builder<T> error(ErrorInfo error) { this.error = error; return this; }
        public Builder<T> metadata(ResponseMetadata metadata) { this.metadata = metadata; return this; }
        public ApiResponse<T> build() { return new ApiResponse<>(this); }
    }
}
