package com.academic.sastracare.exception.otp;

import com.academic.sastracare.exception.base.BaseAppException;

public class OtpRateLimitExceededException extends BaseAppException {
    public OtpRateLimitExceededException(String message) {
        super(message);
    }
}
