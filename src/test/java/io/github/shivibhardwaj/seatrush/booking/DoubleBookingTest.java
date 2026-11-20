package io.github.shivibhardwaj.seatrush.booking;

import io.github.shivibhardwaj.seatrush.TestcontainersConfiguration;
import io.github.shivibhardwaj.seatrush.common.ConflictException;
import io.github.shivibhardwaj.seatrush.event.*;
import io.github.shivibhardwaj.seatrush.user.UserRole;
import io.github.shivibhardwaj.seatrush.user.UserService;
import io.github.shivibhardwaj.seatrush.venue.SeatService;
import io.github.shivibhardwaj.seatrush.venue.Venue;
import io.github.shivibhardwaj.seatrush.venue.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DoubleBookingTest {

    private static final int THREADS = 20;

    @Autowired VenueService venueService;
    @Autowired SeatService seatService;
    @Autowired UserService userService;
    @Autowired EventService eventService;
    @Autowired PriceTierService priceTierService;
    @Autowired EventPublishService eventPublishService;
    @Autowired EventSeatService eventSeatService;
    @Autowired BookingService bookingService;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void onlyOneOfManyConcurrentHoldsOnTheSameSeatWins() throws Exception {
        String unique = UUID.randomUUID().toString();
        Long eventId = eventOnSale(unique);
        Long seatId = eventSeatService.getSeats(eventId).getFirst().getId();
        List<Long> customerIds = customers(unique);

        CountDownLatch start = new CountDownLatch(1);
        List<Future<Object>> futures = new ArrayList<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(THREADS)) {
            for (Long customerId : customerIds) {
                Callable<Object> attempt = () -> {
                    start.await();
                    try {
                        return bookingService.hold(eventId, customerId, Set.of(seatId), UUID.randomUUID());
                    } catch (RuntimeException ex) {
                        return ex;
                    }
                };
                futures.add(pool.submit(attempt));
            }
            start.countDown();
        }

        List<Object> outcomes = new ArrayList<>();
        for (Future<Object> future : futures) {
            outcomes.add(future.get());
        }
        Map<String, Long> byOutcome = outcomes.stream()
                .collect(Collectors.groupingBy(o -> o.getClass().getSimpleName(), Collectors.counting()));
        System.out.println("Outcomes: " + byOutcome);

        assertThat(outcomes).filteredOn(HoldResult.class::isInstance).hasSize(1);
        assertThat(outcomes).filteredOn(o -> !(o instanceof HoldResult))
                .allMatch(o -> o instanceof ConflictException || o instanceof OptimisticLockingFailureException);
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from booking_item where event_seat_id = ?", Long.class, seatId)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "select version from event_seat where id = ?", Long.class, seatId)).isEqualTo(1);
    }

    private Long eventOnSale(String unique){
        Venue venue = venueService.createVenue("Venue " + unique, "Address", "City");
        seatService.createSeats(venue.getId(), "A", List.of("1"), 1);
        Long organizerId = userService.createUser("org-" + unique + "@example.com", "password123",
                "Test", "Organizer", UserRole.ORGANIZER).getId();
        Instant saleOpensAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Event event = eventService.createEvent(venue.getId(), organizerId, "Concert",
                saleOpensAt.plus(7, ChronoUnit.DAYS), saleOpensAt, "Test event", Currency.getInstance("INR"));
        PriceTier tier = priceTierService.createPriceTier("Standard", new BigDecimal("1000.00"), event.getId());
        eventPublishService.publish(event.getId(), Map.of("A", tier.getId()));
        jdbcTemplate.update("update event set sale_opens_at = now() - interval '1 minute' where id = ?", event.getId());
        return event.getId();
    }

    private List<Long> customers(String unique) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            ids.add(userService.createUser("c" + i + "-" + unique + "@example.com", "password123",
                    "Test", "Customer", UserRole.CUSTOMER).getId());
        }
        return ids;
    }
}
