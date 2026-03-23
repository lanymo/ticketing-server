package ticket.system.ticketing_server.domain.event.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.system.ticketing_server.domain.event.dto.EventCreateRequest;
import ticket.system.ticketing_server.domain.event.dto.EventUpdateRequest;
import ticket.system.ticketing_server.domain.event.entity.Event;
import ticket.system.ticketing_server.domain.event.entity.EventStatus;
import ticket.system.ticketing_server.domain.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public void createEvent(EventCreateRequest request){
        Event event = Event.builder()
                .name(request.getName())
                .venue(request.getVenue())
                .startDate(request.getStartDate())
                .totalSeats(request.getTotalSeats())
                .build();
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Transactional(readOnly = true)
    public List<Event> getEvents(){
        return eventRepository.findAll();
    }

    @Transactional
    public Event updateEvent(Long id, EventUpdateRequest request){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.update(request);
        return event;
    }

    @Transactional(readOnly = true)
    public List<Event> getEventByStatus(EventStatus status){
        return eventRepository.findByStatus(status);
    }

    @Transactional
    public void changeStatus(Long id, EventStatus status){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.changeStatus(status);
    }
}
