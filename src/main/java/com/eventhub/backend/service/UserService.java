package com.eventhub.backend.service;

import com.eventhub.backend.dto.UserResponse;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new student account.
    public UserResponse createUser(User user) {

        user.setRole("STUDENT");
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    // Get all users without exposing passwords.
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Change a user's role.
    @Transactional
    public UserResponse updateUserRole(Long id, String requestedRole) {

        if (requestedRole == null || requestedRole.isBlank()) {
            throw new IllegalArgumentException(
                    "Role must not be empty."
            );
        }

        String role = requestedRole
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!role.equals("STUDENT") &&
                !role.equals("ORGANIZER")) {

            throw new IllegalArgumentException(
                    "Role must be STUDENT or ORGANIZER."
            );
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        // Never allow this endpoint to modify an admin account.
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be changed here."
            );
        }

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return convertToResponse(updatedUser);
    }

    // Return safe user details only.
    private UserResponse convertToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}