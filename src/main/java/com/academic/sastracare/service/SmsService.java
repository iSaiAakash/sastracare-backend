package com.academic.sastracare.service;

import com.academic.sastracare.exception.external.ExternalServiceException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.phone.number}")
    private String fromNumber;

    public void sendSms(String to, String message) {

        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    message
            ).create();

        } catch (ApiException e) {
            throw new ExternalServiceException("SMS service failed");
        }
    }
}
