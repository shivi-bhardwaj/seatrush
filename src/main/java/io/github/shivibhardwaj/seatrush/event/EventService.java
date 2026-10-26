package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BusinessRuleException;
import io.github.shivibhardwaj.seatrush.common.NotFoundException;
import io.github.shivibhardwaj.seatrush.user.User;
import io.github.shivibhardwaj.seatrush.user.UserService;
import io.github.shivibhardwaj.seatrush.venue.Venue;
import io.github.shivibhardwaj.seatrush.venue.VenueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Currency;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final EventRepo eventRepo;
    private final VenueService venueService;
    private final UserService userService;

    public EventService(EventRepo eventRepo, VenueService venueService, UserService userService){
        this.eventRepo = eventRepo;
        this.venueService = venueService;
        this.userService = userService;
    }

    @Transactional
    public Event createEvent(Long venueId, Long organizerId, String title,
                             Instant startsAt, Instant saleOpensAt, String description,
                             Currency currency){
        Venue venue = venueService.getVenue(venueId);
        User user = userService.getUser(organizerId);
        if(!user.canOrganizeEvents()){
            throw new BusinessRuleException("User " + organizerId + " cannot organize events");
        }
        return eventRepo.save(new Event(title, description, startsAt, saleOpensAt, user, venue, currency));
    }

    public Event getEvent(Long eventId){
        return eventRepo.findById(eventId).orElseThrow(()-> new NotFoundException("Event", eventId));
    }
}
