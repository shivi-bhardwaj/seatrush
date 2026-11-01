package io.github.shivibhardwaj.seatrush.booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;

public record BookingResponse(
        Long id, Long eventId, BookingStatus status,
        BigDecimal total, Currency currency, Instant holdExpiresAt,
        List<BookingItemResponse> items
) {
    static BookingResponse from(Booking booking, List<BookingItem> items){
        return new BookingResponse(booking.getId(), booking.getEvent().getId(),
                booking.getStatus(), booking.getTotal(), booking.getCurrency(),
                booking.getHoldExpiresAt(), items.stream().map(BookingItemResponse :: from).toList());
    }
}
