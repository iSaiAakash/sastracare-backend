package com.academic.sastracare.controller;

import com.academic.sastracare.dto.AuthResponse;
import com.academic.sastracare.dto.RefreshRequest;
import com.academic.sastracare.dto.RequestOtpRequest;
import com.academic.sastracare.dto.VerifyOtpRequest;
import com.academic.sastracare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(
            @Valid @RequestBody RequestOtpRequest request) {

        authService.requestOtp(request.getMobile());

        return ResponseEntity.ok(
                Map.of("message", "OTP sent successfully")
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        AuthResponse response =
                authService.verifyOtpAndLogin(
                        request.getMobile(),
                        request.getOtp()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshRequest request) {

        AuthResponse response =
                authService.refreshAccessToken(
                        request.getRefreshToken());

        return ResponseEntity.ok(response);
    }
}