package com.example.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Login Request DTO
 *
 * Used for user authentication.
 * Validation happens before hitting service layer.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    /**
     * User email (login identifier)
     */
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Raw password (will be validated against BCrypt hash)
     */
    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
