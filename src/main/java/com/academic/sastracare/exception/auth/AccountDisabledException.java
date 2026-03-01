package com.academic.sastracare.exception.auth;

import com.academic.sastracare.exception.base.BaseAppException;

public class AccountDisabledException extends BaseAppException {
    public AccountDisabledException(String message) {
        super(message);
    }
}