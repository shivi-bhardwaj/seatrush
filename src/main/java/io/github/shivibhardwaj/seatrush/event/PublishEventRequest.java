package io.github.shivibhardwaj.seatrush.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record PublishEventRequest(@NotEmpty Map<String, @NotNull Long> sectionTiers) {
}
