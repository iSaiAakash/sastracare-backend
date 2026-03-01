package com.academic.sastracare.exception.external;

import com.academic.sastracare.exception.base.BaseAppException;

public class ExternalServiceException extends BaseAppException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
