package io.github.shivibhardwaj.seatrush.booking;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BookingRepo extends Repository<Booking, Long> {

    <S extends Booking> S save(S entity);

    Optional<Booking> findById(Long id);
}
