package com.eventhub.backend.service;

import com.eventhub.backend.entity.Event;
import com.eventhub.backend.entity.Registration;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.EventRepository;
import com.eventhub.backend.repository.RegistrationRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            UserRepository userRepository,
            EventRepository eventRepository) {

        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    // Register a student for an event
    public Registration registerStudent(
            Long studentId,
            Long eventId) {

        // Check duplicate registration
        boolean alreadyRegistered =
                registrationRepository.existsByStudentIdAndEventId(
                        studentId,
                        eventId
                );

        if (alreadyRegistered) {
            throw new RuntimeException(
                    "Student is already registered for this event"
            );
        }

        // Find student
        User student = userRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Find event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        // Check event capacity
        long registeredCount =
                registrationRepository.countByEventId(eventId);

        if (event.getCapacity() == null ||
                registeredCount >= event.getCapacity()) {

            throw new RuntimeException("Event is full");
        }

        // Create registration
        Registration registration = new Registration();

        registration.setStudent(student);
        registration.setEvent(event);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus("REGISTERED");

        return registrationRepository.save(registration);
    }

    // Get registrations for a student
    public List<Registration> getStudentRegistrations(
            Long studentId) {

        return registrationRepository.findByStudentId(studentId);
    }

    // Get registrations for a particular event
    public List<Registration> getEventRegistrations(
            Long eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Event not found");
        }

        return registrationRepository.findByEventId(eventId);
    }

    // Count registrations for an event
    public long getRegistrationCount(Long eventId) {

        return registrationRepository.countByEventId(eventId);
    }

    // Cancel a registration
    public void cancelRegistration(Long registrationId) {

        if (!registrationRepository.existsById(registrationId)) {
            throw new RuntimeException(
                    "Registration not found"
            );
        }

        registrationRepository.deleteById(registrationId);
    }
}