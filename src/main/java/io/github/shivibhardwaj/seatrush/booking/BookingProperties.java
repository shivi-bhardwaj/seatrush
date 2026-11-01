package io.github.shivibhardwaj.seatrush.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("seatrush.booking")
@Validated
public record BookingProperties(@NotNull Duration holdDuration, @Positive int maxSeatsPerUser) {

}
