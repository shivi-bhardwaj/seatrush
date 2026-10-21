package io.github.shivibhardwaj.seatrush.user;

public record UserResponse(Long id, String email, String firstName, String lastName, UserRole role) {

    static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }
}
