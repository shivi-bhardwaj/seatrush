package io.github.shivibhardwaj.seatrush.event;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.Currency;

public record CreateEventRequest(@NotBlank @Size(max = 255) String title, String description,
                                 @NotNull @Future Instant startsAt, @NotNull @Future Instant saleOpensAt,
                                 @NotNull Long venueId, @NotNull Long organizerId,
                                 @NotNull Currency currency) {
    @AssertTrue(message = "saleOpensAt must be before startsAt")
    public boolean isSaleOpensBeforeStart(){
        if(startsAt == null || saleOpensAt == null) return true;
        return saleOpensAt.isBefore(startsAt);
    }
}
