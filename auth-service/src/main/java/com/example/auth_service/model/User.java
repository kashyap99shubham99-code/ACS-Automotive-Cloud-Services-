package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")   // explicit table name (good practice)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email is the PRIMARY LOGIN IDENTIFIER
     * Used by:
     * - Login
     * - JWT subject
     * - UserRepository.findByEmail(...)
     */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Password stored as BCrypt HASH
     * Never plain text
     */
    @Column(nullable = false)
    private String password;

    /**
     * Authorization role
     * Example: ROLE_USER, ROLE_ADMIN
     */
    @Column(nullable = false)
    private String role;

    /**
     * Account enabled / disabled
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * Audit field
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
