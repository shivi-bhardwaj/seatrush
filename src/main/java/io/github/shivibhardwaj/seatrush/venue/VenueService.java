package io.github.shivibhardwaj.seatrush.venue;

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
        return venueRepo.findById(venueId).orElseThrow(()->new RuntimeException("venue " + venueId + " not found"));
    }

    @Transactional
    public Venue rename(Long venueId, String newName) {
        Venue venue = venueRepo.findById(venueId).orElseThrow(() -> new RuntimeException("venue " + venueId + " not found"));
        venue.setName(newName);
        return venue;
    }
}
