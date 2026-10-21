package io.github.shivibhardwaj.seatrush.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(@NotBlank @Email @Size(max = 255) String email,
                                @NotBlank @Size(min = 8, max = 72) String password,
                                @NotBlank @Size(max = 100) String firstName,
                                @Size(max = 100) String lastName, @NotNull UserRole role) {
}
