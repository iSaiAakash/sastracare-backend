package com.academic.sastracare.exception.otp;

import com.academic.sastracare.exception.base.BaseAppException;

public class OtpExpiredException extends BaseAppException {

    public OtpExpiredException(String message) {
        super(message);
    }
}