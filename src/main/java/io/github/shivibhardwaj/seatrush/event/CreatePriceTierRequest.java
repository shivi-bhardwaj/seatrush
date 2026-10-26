package io.github.shivibhardwaj.seatrush.event;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreatePriceTierRequest(@NotBlank @Size(max = 100) String name,
                                     @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal price) {
}
