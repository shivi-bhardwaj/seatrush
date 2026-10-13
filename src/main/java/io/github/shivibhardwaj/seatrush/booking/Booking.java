package io.github.shivibhardwaj.seatrush.booking;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import io.github.shivibhardwaj.seatrush.event.Event;
import io.github.shivibhardwaj.seatrush.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(nullable = false)
    private Instant holdExpiresAt;

    @Column(nullable = false)
    private UUID idempotencyKey;

    @Version private Long version;

    public Booking(User user, Event event, BigDecimal total, Currency currency, Instant holdExpiresAt, UUID idempotencyKey) {
        Objects.requireNonNull(total, "total");
        if(total.signum() < 0){
            throw new IllegalArgumentException("total must not be negative: " + total);
        }
        this.status = BookingStatus.HELD;
        this.user = Objects.requireNonNull(user, "user");
        this.event = Objects.requireNonNull(event, "event");
        this.total = total;
        this.currency = Objects.requireNonNull(currency, "currency");
        this.holdExpiresAt = Objects.requireNonNull(holdExpiresAt, "holdExpiresAt");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
    }
}
