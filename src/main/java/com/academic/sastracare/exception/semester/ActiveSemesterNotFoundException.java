package com.academic.sastracare.exception.semester;

import com.academic.sastracare.exception.base.BaseAppException;

public class ActiveSemesterNotFoundException extends BaseAppException {

    public ActiveSemesterNotFoundException(String message) {
        super(message);
    }
}