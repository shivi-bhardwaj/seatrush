package io.github.shivibhardwaj.seatrush.booking;

import java.util.List;

public record HoldResult(Booking booking, List<BookingItem> items,
                         boolean created) {
}
