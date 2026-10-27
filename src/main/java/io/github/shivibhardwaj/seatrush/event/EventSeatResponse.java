package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.venue.Seat;

import java.math.BigDecimal;
import java.util.Currency;

public record EventSeatResponse(Long id, String section, String row, String seatNo,
                                String tierName, BigDecimal price, Currency currency,
                                boolean available) {
    static EventSeatResponse from(EventSeat eventSeat){
        Seat seat = eventSeat.getSeat();
        PriceTier priceTier = eventSeat.getPriceTier();
        return new EventSeatResponse(eventSeat.getId(),
                seat.getSection(), seat.getRowLabel(), seat.getSeatNo(),
                priceTier.getName(), priceTier.getPrice(), priceTier.getCurrency(),
                eventSeat.isAvailable());
    }
}
