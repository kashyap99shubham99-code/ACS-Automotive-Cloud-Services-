package com.example.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Signup Request DTO
 *
 * Used for new user registration.
 * Validation is performed before persisting user.
 */
@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    /**
     * User email (unique identifier)
     */
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Raw password (will be encoded before save)
     */
    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
