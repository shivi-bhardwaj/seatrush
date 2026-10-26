package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BusinessRuleException;
import io.github.shivibhardwaj.seatrush.common.ConflictException;
import io.github.shivibhardwaj.seatrush.common.ConstraintViolations;
import io.github.shivibhardwaj.seatrush.venue.Seat;
import io.github.shivibhardwaj.seatrush.venue.SeatService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventPublishService {

    private final EventService eventService;
    private final SeatService seatService;
    private final PriceTierService priceTierService;
    private final EventSeatRepo eventSeatRepo;

    public EventPublishService(EventService eventService, SeatService seatService,
                               PriceTierService priceTierService,
                               EventSeatRepo eventSeatRepo){
        this.eventService = eventService;
        this.seatService = seatService;
        this.priceTierService = priceTierService;
        this.eventSeatRepo = eventSeatRepo;
    }

    @Transactional
    public int publish(Long eventId, Map<String, Long> sectionTiers){

        //1:  event exists and is still DRAFT
        Event event = eventService.getEvent(eventId);
        if(!event.canPublish()){
            throw new BusinessRuleException("Event " + eventId + " is already " + event.getStatus());
        }

        //2: sale opening time must still be in the future
        if(!event.getSaleOpensAt().isAfter(Instant.now())){
            throw new BusinessRuleException("Sale opening time has already passed for event " + eventId);
        }

        //3: the venue's seats
        List<Seat> seats = seatService.getSeats(event.getVenue().getId());
        if(seats.isEmpty()){
            throw new BusinessRuleException("Venue has no seats");
        }

        //4: this event's tiers, by id
        Map<Long, PriceTier> tiersById = priceTierService.getPriceTiers(eventId).stream()
                .collect(Collectors.toMap(PriceTier::getId, Function.identity()));
        if(tiersById.isEmpty()){
            throw new BusinessRuleException("Event " + eventId + " has no price tiers");
        }

        //5: every venue-section mapped and no unknown sections
        Set<String> venueSections = seats.stream()
                .map(Seat::getSection).collect(Collectors.toSet());

        Set<String> missing = new TreeSet<>(venueSections);
        missing.removeAll(sectionTiers.keySet());
        if(!missing.isEmpty()){
            throw new BusinessRuleException("Sections without a price tier: " + missing);
        }

        Set<String> unknown = new TreeSet<>(sectionTiers.keySet());
        unknown.removeAll(venueSections);
        if(!unknown.isEmpty()){
            throw new BusinessRuleException("Unknown sections: " + unknown);
        }

        //6: every tier id belongs to this event
        for(Long tierId : sectionTiers.values()){
            if(!tiersById.containsKey(tierId)){
                throw new BusinessRuleException("Price tier " + tierId + " does not belong to event " + eventId);
            }
        }

        //7: build and save the inventory
        List<EventSeat> eventSeats = new ArrayList<>();
        for(Seat seat: seats){
            PriceTier tier = tiersById.get(sectionTiers.get(seat.getSection()));
            eventSeats.add(new EventSeat(event, seat, tier));
        }
        try{
            eventSeatRepo.saveAll(eventSeats);
        }catch (DataIntegrityViolationException ex){
            if (ConstraintViolations.isViolation(ex, "uk_event_seat")) {
                throw new ConflictException("Event " + eventId + " is already being published", ex);
            }
            throw ex;
        }

        //8: flip the status
        event.publish();
        return eventSeats.size();
    }
}
