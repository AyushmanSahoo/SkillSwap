package com.skillswap.authservice.service;

import com.skillswap.authservice.config.JwtService;
import com.skillswap.authservice.entity.UserAuth;
import com.skillswap.authservice.entity.dto.AuthResponse;
import com.skillswap.authservice.entity.dto.LoginRequest;
import com.skillswap.authservice.entity.dto.RegisterRequest;
import com.skillswap.authservice.entity.dto.UserCreateRequest;
import com.skillswap.authservice.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WebClient userWebClient;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAuthRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(request.getEmail());
        userAuth.setPassword(passwordEncoder.encode(request.getPassword()));
        
        UserAuth savedUser = userAuthRepository.save(userAuth);
        log.info("Saved new user to auth_db with ID: {}", savedUser.getId());

        UserCreateRequest userProfileRequest = new UserCreateRequest(
                savedUser.getId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );

        try {
            userWebClient.post()
                    .uri("/api/v1/users") 
                    .bodyValue(userProfileRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); 
            log.info("Successfully created user profile in user-service for user ID: {}", savedUser.getId());
        } catch (Exception e) {
            log.error("Failed to create user profile in user-service", e);
            throw new RuntimeException("Failed to create user profile: " + e.getMessage());
        }

        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationMs)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String accessToken = jwtService.generateToken(userAuth);
        String refreshToken = jwtService.generateRefreshToken(userAuth);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationMs)
                .build();
    }
}