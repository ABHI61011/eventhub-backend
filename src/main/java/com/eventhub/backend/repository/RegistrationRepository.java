package com.eventhub.backend.repository;

import com.eventhub.backend.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    // Check duplicate registrations
    boolean existsByStudentIdAndEventId(
            Long studentId,
            Long eventId
    );

    // Count registrations for an event
    long countByEventId(Long eventId);

    // Get registrations for a student
    List<Registration> findByStudentId(Long studentId);

    // Get registrations for an event
    List<Registration> findByEventId(Long eventId);

    // Delete registrations when an event is deleted
    void deleteByEventId(Long eventId);
}