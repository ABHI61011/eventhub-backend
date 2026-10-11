package com.eventhub.backend.service;

import com.eventhub.backend.dto.LoginRequest;
import com.eventhub.backend.dto.LoginResponse;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.UserRepository;
import com.eventhub.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new RuntimeException(
                    "Your account has no assigned role. Contact the administrator.");
        }

        String role = user.getRole().trim().toUpperCase();

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                role
        );

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role,
                token
        );
    }
}