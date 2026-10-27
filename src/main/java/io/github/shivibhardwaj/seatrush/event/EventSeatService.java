package io.github.shivibhardwaj.seatrush.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventSeatService {

    private final EventService eventService;
    private final EventSeatRepo eventSeatRepo;

    public EventSeatService(EventService eventService, EventSeatRepo eventSeatRepo){
        this.eventService = eventService;
        this.eventSeatRepo = eventSeatRepo;
    }

    public List<EventSeat> getSeats(Long eventId){
        eventService.getEvent(eventId);
        return eventSeatRepo.findSeatMap(eventId);
    }
}
