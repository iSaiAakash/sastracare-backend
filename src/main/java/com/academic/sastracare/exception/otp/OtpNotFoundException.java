package com.academic.sastracare.exception.otp;

import com.academic.sastracare.exception.base.BaseAppException;

public class OtpNotFoundException extends BaseAppException {
    public OtpNotFoundException(String message) {
        super(message);
    }
}
