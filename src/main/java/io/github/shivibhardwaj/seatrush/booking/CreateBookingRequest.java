package io.github.shivibhardwaj.seatrush.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateBookingRequest(@NotNull Long userId,
                                   @NotEmpty @Size(max = 10, message = "at most 10 seats per request")
                                   Set<@NotNull Long> eventSeatIds) {
}
