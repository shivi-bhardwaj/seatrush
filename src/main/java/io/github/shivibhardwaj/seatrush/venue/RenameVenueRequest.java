package io.github.shivibhardwaj.seatrush.venue;

import jakarta.validation.constraints.NotBlank;

public record RenameVenueRequest(@NotBlank String name) {}
