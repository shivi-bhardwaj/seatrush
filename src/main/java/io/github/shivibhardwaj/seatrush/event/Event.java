package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import io.github.shivibhardwaj.seatrush.user.User;
import io.github.shivibhardwaj.seatrush.venue.Venue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Currency;

@Entity
@Table(name="event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private Instant saleOpensAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 3)
    private Currency currency;

    public Event(String title, String description, Instant startsAt, Instant saleOpensAt,
                 User organizer, Venue venue, Currency currency) {
        this.title = title;
        this.description = description;
        this.startsAt = startsAt;
        this.saleOpensAt = saleOpensAt;
        this.organizer = organizer;
        this.venue = venue;
        this.status = EventStatus.DRAFT;
        this.currency = currency;
    }

    public boolean canAddPriceTier(){
        return status == EventStatus.DRAFT;
    }
}
