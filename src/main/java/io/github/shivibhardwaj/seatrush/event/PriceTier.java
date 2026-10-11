package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Entity
@Table(name="price_tier")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceTier extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="event_id", nullable=false)
    private Event event;

    public PriceTier(String name, BigDecimal price, Currency currency, Event event) {
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative: " + price);
        }
        this.name = Objects.requireNonNull(name, "name");
        this.price = price;
        this.currency = Objects.requireNonNull(currency, "currency");
        this.event = Objects.requireNonNull(event, "event");
    }
}
