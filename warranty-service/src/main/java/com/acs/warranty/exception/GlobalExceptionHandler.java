package com.acs.warranty.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handlers for the warranty-service.
 * Replaces the old WarrantyControllerAdvice (moved to this package).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
    }

    // JPA (Jakarta) NonUniqueResultException
    @ExceptionHandler(jakarta.persistence.NonUniqueResultException.class)
    public ResponseEntity<Map<String, Object>> handleNonUniqueJpa(jakarta.persistence.NonUniqueResultException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict - multiple records",
                "Multiple active warranties found for the provided VIN; data inconsistent.", request.getRequestURI());
    }

    // Hibernate-specific NonUniqueResultException
    @ExceptionHandler(org.hibernate.NonUniqueResultException.class)
    public ResponseEntity<Map<String, Object>> handleNonUniqueHibernate(org.hibernate.NonUniqueResultException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict - multiple records",
                "Multiple active warranties found for the provided VIN; data inconsistent.", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Data Integrity Violation", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage(), request.getRequestURI());
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}
