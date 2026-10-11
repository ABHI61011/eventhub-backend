package com.eventhub.backend.service;

import com.eventhub.backend.entity.Category;
import com.eventhub.backend.entity.Event;
import com.eventhub.backend.repository.CategoryRepository;
import com.eventhub.backend.repository.EventRepository;
import com.eventhub.backend.repository.RegistrationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(
            EventRepository eventRepository,
            CategoryRepository categoryRepository,
            RegistrationRepository registrationRepository) {

        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatusIgnoreCase("APPROVED");
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));
    }

    public Event createEvent(Event event) {
        if (event.getCategory() == null ||
                event.getCategory().getId() == null) {
            throw new IllegalArgumentException(
                    "Category is required");
        }

        Category category = categoryRepository
                .findById(event.getCategory().getId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        event.setCategory(category);

        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event event) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        if (event.getCategory() == null ||
                event.getCategory().getId() == null) {
            throw new IllegalArgumentException(
                    "Category is required");
        }

        Category category = categoryRepository
                .findById(event.getCategory().getId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setDate(event.getDate());
        existingEvent.setTime(event.getTime());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setCapacity(event.getCapacity());
        existingEvent.setCategory(category);

        return eventRepository.save(existingEvent);
    }

    @Transactional
    public Event updateEventStatus(Long id, String status) {
        if (status == null ||
                (!"APPROVED".equalsIgnoreCase(status) &&
                 !"REJECTED".equalsIgnoreCase(status))) {
            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED");
        }

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        event.setStatus(status.toUpperCase());

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        // Remove registrations referencing this event first.
        registrationRepository.deleteByEventId(id);

        // Then delete the event.
        eventRepository.delete(event);

        // Flush changes so database errors occur inside this transaction.
        eventRepository.flush();
    }
}