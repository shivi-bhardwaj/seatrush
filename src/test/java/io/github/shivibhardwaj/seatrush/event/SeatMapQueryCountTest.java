package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.TestcontainersConfiguration;
import io.github.shivibhardwaj.seatrush.user.User;
import io.github.shivibhardwaj.seatrush.user.UserRole;
import io.github.shivibhardwaj.seatrush.user.UserService;
import io.github.shivibhardwaj.seatrush.venue.SeatService;
import io.github.shivibhardwaj.seatrush.venue.Venue;
import io.github.shivibhardwaj.seatrush.venue.VenueService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import(TestcontainersConfiguration.class)
class SeatMapQueryCountTest {

    @Autowired VenueService venueService;
    @Autowired SeatService seatService;
    @Autowired UserService userService;
    @Autowired EventService eventService;
    @Autowired PriceTierService priceTierService;
    @Autowired EventPublishService eventPublishService;
    @Autowired EventSeatService eventSeatService;
    @Autowired EntityManagerFactory entityManagerFactory;

    @ParameterizedTest
    @ValueSource(ints = {5, 50})
    void seatMapRunsTheSameNumberOfQueriesForAnyNumberOfSeats(int seatsPerRow) {
        Long eventId = publishedEventWithSeats(seatsPerRow);
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        List<EventSeatResponse> seatMap = eventSeatService.getSeats(eventId).stream()
                .map(EventSeatResponse::from)
                .toList();

        assertThat(seatMap).hasSize(seatsPerRow * 2);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
    }

    private Long publishedEventWithSeats(int seatsPerRow) {
        String unique = UUID.randomUUID().toString();
        Venue venue = venueService.createVenue("Venue " + unique, "Address", "City");
        seatService.createSeats(venue.getId(), "A", List.of("1", "2"), seatsPerRow);
        User organizer = userService.createUser(unique + "@example.com", "password123",
                "Test", "Organizer", UserRole.ORGANIZER);
        Instant saleOpensAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Event event = eventService.createEvent(venue.getId(), organizer.getId(), "Concert",
                saleOpensAt.plus(7, ChronoUnit.DAYS), saleOpensAt, "Test event", Currency.getInstance("INR"));
        PriceTier tier = priceTierService.createPriceTier("Standard", new BigDecimal("1000.00"), event.getId());
        eventPublishService.publish(event.getId(), Map.of("A", tier.getId()));
        return event.getId();
    }
}
