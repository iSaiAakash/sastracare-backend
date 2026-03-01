package com.academic.sastracare.service;

import com.academic.sastracare.entity.Otp;
import com.academic.sastracare.exception.otp.*;
import com.academic.sastracare.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OtpService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${otp.expiry.minutes}")
    private int expiryMinutes;

    private static final int MAX_OTP_5_MIN = 3;
    private static final int MAX_OTP_1_DAY = 10;

    // ===================== GENERATE OTP =====================

    public void generateAndSendOtp(String parentId, String phone) {

        phone = phone.trim();

        checkOtpRateLimit(phone);

        String otp = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        String hashedOtp = passwordEncoder.encode(otp);

        // Send SMS first (if SMS fails, DB should not save OTP)
        smsService.sendSms(phone, "Your OTP is: " + otp);

        Otp otpEntity = new Otp();
        otpEntity.setParentId(parentId);
        otpEntity.setPhoneNumber(phone);
        otpEntity.setOtpHash(hashedOtp);
        otpEntity.setExpiryTime(
                LocalDateTime.now().plusMinutes(expiryMinutes)
        );
        otpEntity.setVerified(false);

        otpRepository.save(otpEntity);
    }

    // ===================== RATE LIMIT =====================

    private void checkOtpRateLimit(String phone) {

        phone = phone.trim();

        String key5Min = "otp:5min:" + phone;
        String key1Day = "otp:1day:" + phone;

        Long count5Min = redisTemplate.opsForValue().increment(key5Min);
        Long count1Day = redisTemplate.opsForValue().increment(key1Day);

        if (count5Min == 1) {
            redisTemplate.expire(key5Min, Duration.ofMinutes(5));
        }

        if (count1Day == 1) {
            redisTemplate.expire(key1Day, Duration.ofDays(1));
        }

        if (count5Min != null && count5Min > MAX_OTP_5_MIN) {
            throw new OtpRateLimitExceededException(
                    "Too many OTP requests. Try again after 5 minutes."
            );
        }

        if (count1Day != null && count1Day > MAX_OTP_1_DAY) {
            throw new DailyOtpLimitExceededException(
                    "Daily OTP limit exceeded."
            );
        }
    }

    // ===================== VERIFY OTP =====================

    public void verifyOtp(String phone, String inputOtp) {

        phone = phone.trim();
        inputOtp = inputOtp.trim();

        Otp otp = otpRepository
                .findTopByPhoneNumberOrderByIdDesc(phone)
                .orElseThrow(() ->
                        new OtpNotFoundException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        if (!passwordEncoder.matches(inputOtp, otp.getOtpHash())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
    }

    // ===================== CLEANUP =====================

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}