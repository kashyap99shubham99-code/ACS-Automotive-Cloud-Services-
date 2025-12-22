package com.example.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Secret key (must be at least 256 bits for HS256)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // ⏱ Token validity (24 hours)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // ===============================
    // Generate JWT Token
    // ===============================
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // ===============================
    // Extract Claims
    // ===============================
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===============================
    // Validate Token ✅ (THIS FIXES YOUR ERROR)
    // ===============================
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);

            // check expiration
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
