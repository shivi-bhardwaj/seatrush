package io.github.shivibhardwaj.seatrush.event;

public record PublishEventResponse(Long eventId, EventStatus status, int seatsCreated) {
}
