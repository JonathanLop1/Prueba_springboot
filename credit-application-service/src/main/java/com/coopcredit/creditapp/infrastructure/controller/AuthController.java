package com.coopcredit.creditapp.infrastructure.controller;

import com.coopcredit.creditapp.application.dto.AuthResponse;
import com.coopcredit.creditapp.application.dto.LoginRequest;
import com.coopcredit.creditapp.application.dto.RegisterUserRequest;
import com.coopcredit.creditapp.application.usecase.AuthenticationUseCase;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        log.info("User registration requested for username: {}", request.getUsername());
        AuthResponse response = authenticationUseCase.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        AuthResponse response = authenticationUseCase.login(request);
        log.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
}
