package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import io.github.shivibhardwaj.seatrush.venue.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "event_seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSeat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "price_tier_id", nullable = false)
    private PriceTier  priceTier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventSeatStatus status;

    @Version private Long version;

    public EventSeat(Event event, Seat seat, PriceTier priceTier) {
        this.event = Objects.requireNonNull(event, "event");
        this.seat = Objects.requireNonNull(seat, "seat");
        this.priceTier = Objects.requireNonNull(priceTier, "priceTier");
        this.status = EventSeatStatus.AVAILABLE;
    }

    public boolean isAvailable(){
        return status == EventSeatStatus.AVAILABLE;
    }
}
