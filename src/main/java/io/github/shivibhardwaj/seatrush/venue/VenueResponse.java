package io.github.shivibhardwaj.seatrush.venue;

public record VenueResponse(Long id, String name, String address, String city) {

    static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getCity());
    }
}
