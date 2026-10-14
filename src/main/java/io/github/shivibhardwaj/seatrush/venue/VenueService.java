package io.github.shivibhardwaj.seatrush.venue;

import io.github.shivibhardwaj.seatrush.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepo venueRepo;

    public VenueService(VenueRepo venueRepo){
        this.venueRepo = venueRepo;
    }

    @Transactional
    public Venue createVenue(String name, String address, String city) {
        if(city.isBlank()){
            throw new IllegalArgumentException("city is blank");
        }
        return venueRepo.save(new Venue(name, address, city));
    }

    public Venue getVenue(Long venueId) {
        return findVenue(venueId);
    }

    @Transactional
    public Venue rename(Long venueId, String newName) {
        Venue venue = findVenue(venueId);
        venue.setName(newName);
        return venue;
    }

    private Venue findVenue(Long venueId) {
        return venueRepo.findById(venueId)
                .orElseThrow(() -> new NotFoundException("Venue", venueId));
    }
}
