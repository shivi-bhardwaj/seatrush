package io.github.shivibhardwaj.seatrush.venue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepo extends JpaRepository<Seat, Long> {

    List<Seat> findByVenueId(Long venueId);
}
