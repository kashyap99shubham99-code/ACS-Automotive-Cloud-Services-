package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authentication Response DTO
 *
 * Returned after successful login/signup.
 * Contains JWT token only.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT token (Bearer token)
     */
    private String token;
}
