package io.github.shivibhardwaj.seatrush.user;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    public User(String email, String passwordHash, String firstName, String lastName, UserRole role) {
        this.email = Objects.requireNonNull(email, "email").toLowerCase(Locale.ROOT);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.firstName = Objects.requireNonNull(firstName, "firstName");
        this.lastName = lastName;
        this.role = Objects.requireNonNull(role, "role");
    }
}
