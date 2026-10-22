package io.github.shivibhardwaj.seatrush.venue;

import io.github.shivibhardwaj.seatrush.common.ConflictException;
import io.github.shivibhardwaj.seatrush.common.ConstraintViolations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    private final VenueService venueService;
    private final SeatRepo seatRepo;

    public SeatService(VenueService venueService, SeatRepo seatRepo) {
        this.venueService = venueService;
        this.seatRepo = seatRepo;
    }

    @Transactional
    public int createSeats(Long venueId, String section, List<String> rows, int seatsPerRow) {
        Venue venue = venueService.getVenue(venueId);

        List<Seat> seats = new ArrayList<>();
        for (String row : rows) {
            for(int n = 1; n <=seatsPerRow; n++) {
                seats.add(new Seat(String.valueOf(n), row, section, venue));
            }
        }
        try {
            seatRepo.saveAll(seats);
        } catch (DataIntegrityViolationException ex) {
            if (ConstraintViolations.isViolation(ex, "uk_seat_position")) {
                throw new ConflictException(
                        "Seats already exist in venue " + venueId + ", section " + section, ex);
            }
            throw ex;
        }
        return seats.size();
    }
}
