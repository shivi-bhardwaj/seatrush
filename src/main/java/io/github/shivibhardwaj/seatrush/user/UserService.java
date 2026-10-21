package io.github.shivibhardwaj.seatrush.user;

import io.github.shivibhardwaj.seatrush.common.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Transactional
    public User createUser(String email, String rawPassword, String firstName, String lastName, UserRole role) {
        String passwordHash = passwordEncoder.encode(rawPassword);
        return userRepo.save(new User(email,passwordHash, firstName, lastName, role));
    }

    public User getUser(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
    }
}
