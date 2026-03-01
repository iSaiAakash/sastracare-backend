package com.academic.sastracare.exception.auth;

import com.academic.sastracare.exception.base.BaseAppException;

public class UnauthorizedAccessException extends BaseAppException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}