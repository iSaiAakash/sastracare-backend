package com.academic.sastracare.exception;

import com.academic.sastracare.exception.base.BaseAppException;
import com.academic.sastracare.exception.external.ExternalServiceException;
import com.academic.sastracare.exception.validation.InvalidRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------------- DOMAIN ----------------
    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<?> handleDomain(BaseAppException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        if (ex instanceof InvalidRequestException) {
            status = HttpStatus.BAD_REQUEST;
            code = ErrorCode.VALIDATION_ERROR;
        }

        if (ex instanceof ExternalServiceException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            code = ErrorCode.EXTERNAL_FAILURE;
        }

        if (status.is4xxClientError()) {
            log.warn("Client error: {}", ex.getMessage());
        } else {
            log.error("Domain error", ex);
        }

        return build(status, ex.getMessage(), code);
    }

    // ---------------- REDIS ----------------
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<?> handleRedis(RedisConnectionFailureException ex) {

        log.error("Redis unavailable", ex);

        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "Cache service unavailable",
                ErrorCode.EXTERNAL_FAILURE);
    }

    // ---------------- DATABASE ----------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDb(DataIntegrityViolationException ex) {

        log.error("Database constraint violation", ex);

        return build(HttpStatus.CONFLICT,
                "Database constraint violation",
                ErrorCode.INTERNAL_ERROR);
    }

    // ---------------- FALLBACK ----------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception ex) {

        log.error("Unexpected error", ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected internal server error",
                ErrorCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status,
                                                   String message,
                                                   ErrorCode code) {

        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        code.getCode(),
                        message
                )
        );
    }
}