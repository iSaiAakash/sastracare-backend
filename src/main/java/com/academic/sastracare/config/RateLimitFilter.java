package com.academic.sastracare.config;

import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> otpBuckets = new ConcurrentHashMap<>();

    private Bucket createOtpBucket() {
        return Bucket4j.builder()
                // 5 OTP attempts per 5 minutes per IP
                .addLimit(Bandwidth.classic(
                        5,
                        Refill.intervally(5, Duration.ofMinutes(5))
                ))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔐 Apply rate limiting ONLY for OTP endpoints
        if (uri.startsWith("/auth/request-otp") ||
                uri.startsWith("/auth/verify-otp")) {

            String ip = request.getRemoteAddr();
            Bucket bucket = otpBuckets.computeIfAbsent(ip, k -> createOtpBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\":\"Too many OTP attempts. Please try again later.\"}"
                );
                return;
            }
        }

        // ✅ All other APIs are NOT rate limited
        filterChain.doFilter(request, response);
    }
}