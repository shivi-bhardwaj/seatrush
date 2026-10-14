package io.github.shivibhardwaj.seatrush.venue;

import jakarta.validation.constraints.NotBlank;

public record CreateVenueRequest(@NotBlank String name,
                                 @NotBlank String address,
                                 @NotBlank String city) {}
