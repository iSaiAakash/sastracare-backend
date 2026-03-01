package com.academic.sastracare.exception;

public enum ErrorCode {

    AUTH_UNAUTHORIZED("AUTH-001"),
    AUTH_INVALID_TOKEN("AUTH-002"),

    OTP_INVALID("OTP-001"),
    OTP_EXPIRED("OTP-002"),
    OTP_RATE_LIMIT("OTP-003"),

    SEMESTER_NOT_FOUND("SEM-001"),
    DATA_NOT_AVAILABLE("SEM-002"),

    EXTERNAL_FAILURE("EXT-001"),
    VALIDATION_ERROR("VAL-001"),

    INTERNAL_ERROR("SYS-001");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}