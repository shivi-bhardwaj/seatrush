package io.github.shivibhardwaj.seatrush.venue;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;
    private final SeatService seatService;

    public VenueController(VenueService venueService, SeatService seatService) {
        this.venueService = venueService;
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody CreateVenueRequest request){
        Venue venue = venueService.createVenue(request.name(), request.address(), request.city());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(venue.getId())
                .toUri();
        return ResponseEntity.created(location).body(VenueResponse.from(venue));
    }

    @GetMapping("/{id}")
    public VenueResponse get(@PathVariable Long id){
        return VenueResponse.from(venueService.getVenue(id));
    }

    @PatchMapping("/{id}")
    public VenueResponse rename(@PathVariable Long id, @Valid @RequestBody RenameVenueRequest request){
        return VenueResponse.from(venueService.rename(id, request.name()));
    }

    @PostMapping("/{venueId}/seats")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSeatsResponse createSeats(@PathVariable Long venueId,
                                           @Valid @RequestBody CreateSeatsRequest request){
        int created = seatService.createSeats(venueId, request.section(), request.rows(), request.seatsPerRow());
        return new CreateSeatsResponse(venueId, request.section(), created);
    }
}
