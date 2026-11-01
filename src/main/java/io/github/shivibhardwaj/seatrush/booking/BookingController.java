package io.github.shivibhardwaj.seatrush.booking;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/events/{eventId}/bookings")
    public ResponseEntity<BookingResponse> hold(@PathVariable Long eventId,
                                                @RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                                @Valid @RequestBody CreateBookingRequest request){
        HoldResult result = bookingService.hold(eventId, request.userId(), request.eventSeatIds(), idempotencyKey);
        BookingResponse body = BookingResponse.from(result.booking(), result.items());
        if(!result.created()){
            return ResponseEntity.ok(body);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/bookings/{id}")
                .buildAndExpand(result.booking().getId())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/bookings/{id}")
    public BookingResponse get(@PathVariable Long id){
        HoldResult result = bookingService.getBooking(id);
        return BookingResponse.from(result.booking(), result.items());
    }
}
