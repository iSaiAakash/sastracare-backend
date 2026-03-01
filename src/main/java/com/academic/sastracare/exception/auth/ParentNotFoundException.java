package com.academic.sastracare.exception.auth;

import com.academic.sastracare.exception.base.BaseAppException;

public class ParentNotFoundException extends BaseAppException {
    public ParentNotFoundException(String message) {
        super(message);
    }
}
