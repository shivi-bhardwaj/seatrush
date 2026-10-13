package io.github.shivibhardwaj.seatrush.booking;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BookingItemRepo extends Repository<BookingItem, Long> {

    <S extends BookingItem> S save(S entity);

    Optional<BookingItem> findById(Long id);
}
