package com.academic.sastracare.exception.auth;

import com.academic.sastracare.exception.base.BaseAppException;

public class InvalidRefreshTokenException extends BaseAppException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}