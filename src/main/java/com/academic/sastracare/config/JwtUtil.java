package com.academic.sastracare.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiry.minutes}")
    private long accessExpiryMinutes;

    @Value("${jwt.refresh.expiry.days}")
    private long refreshExpiryDays;

    private Key secretKey;

    @PostConstruct
    public void init() {

        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 characters long"
            );
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // =========================
    // ACCESS TOKEN
    // =========================
    public String generateAccessToken(String parentId) {

        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + accessExpiryMinutes * 60 * 1000
        );

        return Jwts.builder()
                .setSubject(parentId)
                .claim("type", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    public String generateRefreshToken(String parentId) {

        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + refreshExpiryDays * 24 * 60 * 60 * 1000
        );

        return Jwts.builder()
                .setSubject(parentId)
                .claim("type", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // VALIDATION
    // =========================
    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // =========================
    // PARSE TOKEN
    // =========================
    private Claims parseToken(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expired");
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 SecurityException |
                 IllegalArgumentException e) {

            throw new JwtException("Invalid token");
        }
    }

    // =========================
    // HELPERS
    // =========================
    public String extractParentId(String token) {
        return parseToken(token).getSubject();
    }

    public String extractTokenType(String token) {
        return parseToken(token).get("type", String.class);
    }
}