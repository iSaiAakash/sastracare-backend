package com.academic.sastracare.exception.semester;

import com.academic.sastracare.exception.base.BaseAppException;

public class DataNotAvailableException extends BaseAppException {
    public DataNotAvailableException(String message) {
        super(message);
    }
}