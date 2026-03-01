package com.academic.sastracare.exception.auth;

import com.academic.sastracare.exception.base.BaseAppException;

public class InvalidCredentialsException extends BaseAppException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}