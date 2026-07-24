package com.primus.server.api;

import com.primus.common.dto.ApiResponse;
import com.primus.common.exception.PrimusException;
import com.primus.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions into standardised {@link ApiResponse} error envelopes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        log.debug("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), 400, ex.getDetails()));
    }

    @ExceptionHandler(com.primus.common.exception.AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(com.primus.common.exception.AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), 401));
    }

    @ExceptionHandler(com.primus.common.exception.AuthorizationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthZ(com.primus.common.exception.AuthorizationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), 403));
    }

    @ExceptionHandler(com.primus.common.exception.NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(com.primus.common.exception.NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), 404));
    }

    @ExceptionHandler(PrimusException.class)
    public ResponseEntity<ApiResponse<Void>> handlePrimus(PrimusException ex) {
        log.error("Platform error [{}]: {}", ex.getErrorCode(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), 500));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred", 500));
    }
}
