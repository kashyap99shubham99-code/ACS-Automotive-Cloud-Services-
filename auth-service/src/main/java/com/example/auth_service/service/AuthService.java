package com.example.auth_service.service;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.SignupRequest;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ===============================
    // SIGNUP
    // ===============================
    public void signup(SignupRequest request) {

        log.info("Signup attempt for email={}", request.getEmail());

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalStateException(
                            "User already exists with email=" + request.getEmail()
                    );
                });

        User user = User.builder()
                .email(request.getEmail())                           // ✅ FIXED
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        log.info("User registered successfully: {}", request.getEmail());
    }

    // ===============================
    // LOGIN (RETURNS JWT)
    // ===============================
    public String login(LoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalStateException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())
        ) {
            throw new IllegalStateException("Invalid email or password");
        }

        // ✅ Generate JWT using EMAIL as subject
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("JWT generated successfully for email={}", request.getEmail());

        return token;
    }
}
