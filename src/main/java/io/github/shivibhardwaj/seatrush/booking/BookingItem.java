package io.github.shivibhardwaj.seatrush.booking;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import io.github.shivibhardwaj.seatrush.event.EventSeat;
import io.github.shivibhardwaj.seatrush.event.PriceTier;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Entity
@Table(name = "booking_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_seat_id", nullable = false)
    private EventSeat eventSeat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private Currency currency;

    public BookingItem(Booking booking, EventSeat eventSeat) {
        this.booking = Objects.requireNonNull(booking, "booking");
        this.eventSeat = Objects.requireNonNull(eventSeat, "eventSeat");

        if(!eventSeat.getEvent().getId().equals(booking.getEvent().getId())){
            throw new IllegalArgumentException("event seat does not belong to this seat");
        }

        PriceTier priceTier = eventSeat.getPriceTier();
        if(!priceTier.getCurrency().equals(booking.getCurrency())){
            throw new IllegalArgumentException("seat currency does not match booking currency");
        }

        this.price = priceTier.getPrice();
        this.currency = priceTier.getCurrency();
    }
}
