package com.eventhub.backend.controller;

import com.eventhub.backend.dto.UserResponse;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.service.UserService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ADMIN: View all users except admin accounts.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .filter(user ->
                        !"ADMIN".equalsIgnoreCase(user.getRole()))
                .toList();
    }

    // Public signup: new accounts are always STUDENT.
    @PostMapping
    public UserResponse createUser(@RequestBody User user) {
        user.setRole("STUDENT");
        return userService.createUser(user);
    }

    // ADMIN: Change a student's or organizer's role.
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String role = request.get("role");

        return userService.updateUserRole(id, role);
    }
}