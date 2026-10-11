package com.eventhub.backend.repository;

import com.eventhub.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Fetch only approved events.
    List<Event> findByStatusIgnoreCase(String status);

}