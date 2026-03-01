package com.academic.sastracare.service;

import com.academic.sastracare.config.JwtUtil;
import com.academic.sastracare.dto.AuthResponse;
import com.academic.sastracare.entity.Parent;
import com.academic.sastracare.exception.auth.InvalidRefreshTokenException;
import com.academic.sastracare.exception.auth.ParentNotFoundException;
import com.academic.sastracare.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final ParentRepository parentRepository;
    private final JwtUtil jwtUtil;

    // =========================
    // REQUEST OTP
    // =========================
    public void requestOtp(String mobile) {

        mobile = normalizeMobile(mobile);

        Parent parent = parentRepository
                .findByMobile(mobile)
                .orElseThrow(() ->
                        new ParentNotFoundException("Parent not registered"));

        otpService.generateAndSendOtp(parent.getId(), mobile);
    }

    // =========================
    // VERIFY OTP + LOGIN
    // =========================
    public AuthResponse verifyOtpAndLogin(String mobile, String otp) {

        mobile = normalizeMobile(mobile);
        otp = otp.trim();

        // Verify OTP first
        otpService.verifyOtp(mobile, otp);

        // Fetch parent
        Parent parent = parentRepository
                .findByMobile(mobile)
                .orElseThrow(() ->
                        new ParentNotFoundException("Parent not registered"));

        // Access token and refresh token use parentId (String)
        String accessToken = jwtUtil.generateAccessToken(parent.getId());
        String refreshToken = jwtUtil.generateRefreshToken(parent.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    public AuthResponse refreshAccessToken(String refreshToken) {

        // Validate signature + expiry first
        if (!jwtUtil.isValid(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        // Ensure token type is REFRESH
        if (!"REFRESH".equals(jwtUtil.extractTokenType(refreshToken))) {
            throw new InvalidRefreshTokenException("Invalid refresh token type");
        }

        String parentId = jwtUtil.extractParentId(refreshToken);

        // Ensure parent still exists
        parentRepository.findById(parentId)
                .orElseThrow(() ->
                        new ParentNotFoundException("Parent not found"));

        String newAccessToken =
                jwtUtil.generateAccessToken(parentId);

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                "Bearer"
        );
    }

    // =========================
    // MOBILE NORMALIZATION
    // =========================
    private String normalizeMobile(String mobile) {

        if (mobile == null || mobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number is required");
        }

        // Remove all non-digits
        mobile = mobile.replaceAll("\\D", "");

        // India-only assumption (modify if multi-country needed)
        if (!mobile.startsWith("91")) {
            mobile = "91" + mobile;
        }

        return "+" + mobile;
    }
}