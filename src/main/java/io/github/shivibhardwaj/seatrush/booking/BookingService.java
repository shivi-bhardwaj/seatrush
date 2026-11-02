package io.github.shivibhardwaj.seatrush.booking;

import io.github.shivibhardwaj.seatrush.common.BusinessRuleException;
import io.github.shivibhardwaj.seatrush.common.ConflictException;
import io.github.shivibhardwaj.seatrush.common.ConstraintViolations;
import io.github.shivibhardwaj.seatrush.common.NotFoundException;
import io.github.shivibhardwaj.seatrush.event.Event;
import io.github.shivibhardwaj.seatrush.event.EventSeat;
import io.github.shivibhardwaj.seatrush.event.EventSeatRepo;
import io.github.shivibhardwaj.seatrush.event.EventService;
import io.github.shivibhardwaj.seatrush.user.User;
import io.github.shivibhardwaj.seatrush.user.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private static final Set<BookingStatus> ACTIVE_STATUSES = EnumSet.of(BookingStatus.HELD, BookingStatus.CONFIRMED);

    private final BookingRepo bookingRepo;
    private final BookingItemRepo bookingItemRepo;
    private final EventSeatRepo eventSeatRepo;
    private final EventService eventService;
    private final UserService userService;
    private final BookingProperties properties;

    public BookingService(BookingRepo bookingRepo, BookingItemRepo bookingItemRepo,
                          EventSeatRepo eventSeatRepo, EventService eventService,
                          UserService userService, BookingProperties properties){
        this.bookingRepo = bookingRepo;
        this.bookingItemRepo = bookingItemRepo;
        this.eventSeatRepo = eventSeatRepo;
        this.eventService = eventService;
        this.userService = userService;
        this.properties = properties;
    }

    @Transactional
    public HoldResult hold(Long eventId, Long userId, Set<Long> eventSeatIds, UUID idempotencyKey){
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

        //1: retry of an earlier request with the same key -> same booking back
        Optional<Booking> existing = bookingRepo.findByIdempotencyKey(idempotencyKey);
        if(existing.isPresent()){
            return replay(existing.get(), eventId, userId, eventSeatIds);
        }

        //2: event exists and is on sale right now
        Event event = eventService.getEvent(eventId);
        if(!event.canBook(now)){
            throw new BusinessRuleException("Event " + eventId + " is not open for booking (status "
                    + event.getStatus() + ", sale opens " + event.getSaleOpensAt()
                    + ", starts " + event.getStartsAt() + ")");
        }

        //3: user exists
        User user = userService.getUser(userId);

        //4: every requested seat belongs to this event
        List<EventSeat> seats = eventSeatRepo.findForHold(eventId, eventSeatIds);
        if(seats.size() != eventSeatIds.size()){
            Set<Long> unknown = new TreeSet<>(eventSeatIds);
            seats.forEach(seat -> unknown.remove(seat.getId()));
            throw new BusinessRuleException("Seats not found for event " + eventId + ": " + unknown);
        }

        //5: every seat is still available
        List<Long> taken = seats.stream()
                .filter(seat -> !seat.canHold())
                .map(EventSeat::getId)
                .sorted()
                .toList();
        if(!taken.isEmpty()){
            throw new ConflictException("Seats already taken: " + taken);
        }

        //6: per-user limit across this user's active bookings for the event
        long alreadyHeld = bookingItemRepo.countActiveSeats(userId, eventId, ACTIVE_STATUSES);
        if(alreadyHeld + seats.size() > properties.maxSeatsPerUser()){
            throw new BusinessRuleException("At most " + properties.maxSeatsPerUser()
                    + " seats per user for this event; user already has " + alreadyHeld);
        }

        //7: total from the current tier prices
        BigDecimal total = seats.stream()
                .map(seat -> seat.getPriceTier().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Instant holdExpiresAt = now.plus(properties.holdDuration());

        //8: the booking; a concurrent request with the same key loses here
        Booking booking = new Booking(user, event, total, event.getCurrency(), holdExpiresAt, idempotencyKey);
        try{
            bookingRepo.save(booking);
        }catch (DataIntegrityViolationException ex){
            if(ConstraintViolations.isViolation(ex, "uk_booking_idempotency")){
                throw new ConflictException("A request with this idempotency key is in progress, retry", ex);
            }
            throw ex;
        }

        //9: items + hold the seats (dirty-checked; @Version guards the race at flush)
        List<BookingItem> items = bookingItemRepo.saveAll(
                seats.stream().map(seat -> new BookingItem(booking, seat)).toList());
        seats.forEach(EventSeat::hold);

        return new HoldResult(booking, items, true);
    }

    public HoldResult getBooking(Long bookingId){
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking", bookingId));
        return new HoldResult(booking, bookingItemRepo.findForResponse(bookingId), false);
    }

    private HoldResult replay(Booking booking, Long eventId, Long userId, Set<Long> eventSeatIds){
        List<BookingItem> items = bookingItemRepo.findForResponse(booking.getId());
        Set<Long> bookedSeatIds = items.stream()
                .map(item -> item.getEventSeat().getId())
                .collect(Collectors.toSet());

        boolean sameRequest = booking.getEvent().getId().equals(eventId)
                && booking.getUser().getId().equals(userId)
                && bookedSeatIds.equals(eventSeatIds);
        if(!sameRequest){
            throw new BusinessRuleException("Idempotency key was already used for a different request");
        }
        return new HoldResult(booking, items, false);
    }
}
