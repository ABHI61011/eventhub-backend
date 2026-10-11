package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Registration;
import com.eventhub.backend.service.RegistrationService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService) {

        this.registrationService = registrationService;
    }

    // Register a student
    @PostMapping("/{studentId}/{eventId}")
    public Registration registerStudent(
            @PathVariable Long studentId,
            @PathVariable Long eventId) {

        return registrationService.registerStudent(
                studentId,
                eventId
        );
    }

    // Get registrations for a student
    @GetMapping("/student/{studentId}")
    public List<Registration> getStudentRegistrations(
            @PathVariable Long studentId) {

        return registrationService.getStudentRegistrations(
                studentId
        );
    }

    // Get registered students for an event
    @GetMapping("/event/{eventId}")
    public List<Registration> getEventRegistrations(
            @PathVariable Long eventId) {

        return registrationService.getEventRegistrations(
                eventId
        );
    }

    // Get the registration count for an event
    @GetMapping("/event/{eventId}/count")
    public long getRegistrationCount(
            @PathVariable Long eventId) {

        return registrationService.getRegistrationCount(
                eventId
        );
    }

    // Cancel a registration
    @DeleteMapping("/{registrationId}")
    public void cancelRegistration(
            @PathVariable Long registrationId) {

        registrationService.cancelRegistration(
                registrationId
        );
    }
}