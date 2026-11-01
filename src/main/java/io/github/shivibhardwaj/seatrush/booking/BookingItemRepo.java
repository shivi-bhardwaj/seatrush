package io.github.shivibhardwaj.seatrush.booking;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingItemRepo extends Repository<BookingItem, Long> {

    <S extends BookingItem> S save(S entity);

    Optional<BookingItem> findById(Long id);

    <S extends BookingItem> List<S> saveAll(Iterable<S> items);

    @Query("""
        select bi from BookingItem bi
        join fetch bi.eventSeat es
        join fetch es.seat
        join fetch es.priceTier
        where bi.booking.id = :bookingId
        order by bi.id
    """)
    List<BookingItem> findForResponse(Long bookingId);

    @Query("""
        select count(bi) from BookingItem bi
        where bi.booking.user.id = :userId
        and bi.booking.event.id = :eventId
        and bi.booking.status in :statuses
    """)
    long countActiveSeats(Long userId, Long eventId, Collection<BookingStatus> statuses);
}
