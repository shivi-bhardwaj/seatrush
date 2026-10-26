package io.github.shivibhardwaj.seatrush.event;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final PriceTierService priceTierService;

    public EventController(EventService eventService, PriceTierService priceTierService) {
        this.eventService = eventService;
        this.priceTierService = priceTierService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request.venueId(), request.organizerId(),
                request.title(), request.startsAt(), request.saleOpensAt(),
                request.description(), request.currency());
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

    @PostMapping("/{eventId}/price-tiers")
    @ResponseStatus(HttpStatus.CREATED)
    public PriceTierResponse createPriceTier(@PathVariable Long eventId, @Valid @RequestBody CreatePriceTierRequest request){
        PriceTier priceTier = priceTierService.createPriceTier(request.name(), request.price(), eventId);
        return PriceTierResponse.from(priceTier);
    }

    @GetMapping("/{eventId}/price-tiers")
    public List<PriceTierResponse> getEventPriceTier(@PathVariable Long eventId){
        return priceTierService.getPriceTiers(eventId).stream()
                .map(PriceTierResponse::from)
                .toList();
    }
}
