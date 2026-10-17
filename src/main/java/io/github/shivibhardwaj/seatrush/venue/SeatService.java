package io.github.shivibhardwaj.seatrush.venue;

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
        seatRepo.saveAll(seats);
        return seats.size();
    }
}
