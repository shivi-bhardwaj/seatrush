package io.github.shivibhardwaj.seatrush.booking;

import io.github.shivibhardwaj.seatrush.event.EventSeat;
import io.github.shivibhardwaj.seatrush.venue.Seat;

import java.math.BigDecimal;

public record BookingItemResponse(Long id, Long eventSeatId, String section, String row,
                                  String seatNo, String tierName, BigDecimal price) {
    static BookingItemResponse from(BookingItem item){
        EventSeat eventSeat = item.getEventSeat();
        Seat seat = eventSeat.getSeat();
        return new BookingItemResponse(item.getId(), eventSeat.getId(), seat.getSection(),
                seat.getRowLabel(), seat.getSeatNo(), eventSeat.getPriceTier().getName(),
                item.getPrice());
    }
}
