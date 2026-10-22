package io.github.shivibhardwaj.seatrush.event;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request.venueId(), request.organizerId(),
                request.title(), request.startsAt(), request.saleOpensAt(),
                request.description());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(event.getId())
                .toUri();
        return ResponseEntity.created(location).body(EventResponse.from(event));
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) {
        return EventResponse.from(eventService.getEvent(id));
    }
}
