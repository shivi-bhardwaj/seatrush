package io.github.shivibhardwaj.seatrush.event;

import java.time.Instant;
import java.util.Currency;

public record EventResponse(Long id, String title, String description,
                            Instant startsAt, Instant saleOpensAt,
                            EventStatus status, Long venueId, Long organizerId,
                            Currency currency) {
    static EventResponse from(Event event){
        return new EventResponse(event.getId(), event.getTitle(), event.getDescription(),
                event.getStartsAt(), event.getSaleOpensAt(), event.getStatus(), event.getVenue().getId(),
                event.getOrganizer().getId(), event.getCurrency());
    }
}
