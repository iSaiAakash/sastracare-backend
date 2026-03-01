package com.academic.sastracare.exception.validation;

import com.academic.sastracare.exception.base.BaseAppException;

public class InvalidRequestException extends BaseAppException {

    public InvalidRequestException(String message) {
        super(message);
    }
}