package com.academic.sastracare.exception.otp;

import com.academic.sastracare.exception.base.BaseAppException;

public class DailyOtpLimitExceededException extends BaseAppException {

    public DailyOtpLimitExceededException(String message) {
        super(message);
    }
}
