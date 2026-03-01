package com.academic.sastracare.exception.semester;

import com.academic.sastracare.exception.base.BaseAppException;

public class NoStudentLinkedException extends BaseAppException {

    public NoStudentLinkedException(String message) {
        super(message);
    }
}