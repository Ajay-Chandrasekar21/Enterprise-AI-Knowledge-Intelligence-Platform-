package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.security.dto.JwtResponse;
import com.enterprise.eakip.security.dto.LoginRequest;
import com.enterprise.eakip.security.dto.RegisterRequest;
import com.enterprise.eakip.security.dto.TokenRefreshRequest;
import com.enterprise.eakip.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Gateway", description = "Endpoints for user registration, token acquisition, and validation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user and profile", description = "Creates a database entry for user credentials and initializes profile mappings")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = authService.registerUser(request);
        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully", registeredUser), 
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials", description = "Validates username and password, returning short-lived JWT access and long-lived refresh tokens")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renew expired JWT access tokens", description = "Validates active refresh tokens to provision replacement access tokens")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
}
