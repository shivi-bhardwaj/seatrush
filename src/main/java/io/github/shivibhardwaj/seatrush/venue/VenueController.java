package io.github.shivibhardwaj.seatrush.venue;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
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
}
