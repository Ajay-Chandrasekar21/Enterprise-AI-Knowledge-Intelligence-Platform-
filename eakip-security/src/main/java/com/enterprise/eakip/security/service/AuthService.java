package com.enterprise.eakip.security.service;

import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.exception.UnauthorizedException;
import com.enterprise.eakip.core.domain.model.Profile;
import com.enterprise.eakip.core.domain.model.Role;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.core.domain.repository.UserRepository;
import com.enterprise.eakip.security.dto.JwtResponse;
import com.enterprise.eakip.security.dto.LoginRequest;
import com.enterprise.eakip.security.dto.RegisterRequest;
import com.enterprise.eakip.security.dto.TokenRefreshRequest;
import com.enterprise.eakip.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Email is already in use");
        }

        Role userRole = Role.STUDENT;
        if (request.getRole() != null) {
            try {
                userRole = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role specified. Supported: STUDENT, FACULTY, LIBRARIAN, ADMIN");
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .libraryCardNumber("LCRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .department(request.getDepartment())
                .preferences("{}")
                .build();

        user.setProfile(profile);
        return userRepository.save(user);
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("STUDENT");

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }

    public JwtResponse refreshAccessToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        try {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenProvider.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
                
                String role = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority().replace("ROLE_", ""))
                        .findFirst()
                        .orElse("STUDENT");

                return JwtResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .username(userDetails.getUsername())
                        .role(role)
                        .build();
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired refresh token: " + e.getMessage());
        }
        throw new UnauthorizedException("Invalid refresh token validation");
    }
}
