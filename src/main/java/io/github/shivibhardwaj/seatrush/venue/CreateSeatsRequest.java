package io.github.shivibhardwaj.seatrush.venue;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateSeatsRequest(@NotBlank @Size(max = 50) String section,
                                 @NotEmpty @Size(max = 100) List<@NotBlank @Size(max = 10) String> rows,
                                 @NotNull @Min(1) @Max(200) Integer seatsPerRow) {

}
