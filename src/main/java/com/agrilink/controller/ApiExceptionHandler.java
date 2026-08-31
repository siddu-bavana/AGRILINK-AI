package com.agrilink.controller;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(Exception e) {
        return ResponseEntity.status(404).body(Map.of("message", "Record not found"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getDefaultMessage())
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Invalid input parameters");
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> bad(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() == null ? "Invalid request" : e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> forbidden(Exception e) {
        return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
    }
}

