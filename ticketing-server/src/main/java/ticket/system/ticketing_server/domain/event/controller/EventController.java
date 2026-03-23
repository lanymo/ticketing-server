package ticket.system.ticketing_server.domain.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ticket.system.ticketing_server.domain.event.dto.EventCreateRequest;
import ticket.system.ticketing_server.domain.event.dto.EventUpdateRequest;
import ticket.system.ticketing_server.domain.event.entity.Event;
import ticket.system.ticketing_server.domain.event.entity.EventStatus;
import ticket.system.ticketing_server.domain.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public void createEvent(@RequestBody EventCreateRequest request) {
        eventService.createEvent(request);
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest request) {
        return eventService.updateEvent(id, request);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestParam EventStatus status) {
        eventService.changeStatus(id, status);
    }

    @GetMapping("/status")
    public List<Event> getEventByStatus(@RequestParam EventStatus status) {
        return eventService.getEventByStatus(status);
    }
}
