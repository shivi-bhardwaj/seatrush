package io.github.shivibhardwaj.seatrush.event;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface EventSeatRepo extends Repository<EventSeat, Long> {

    <S extends EventSeat> S save(S entity);

    <S extends EventSeat> List<S> saveAll(Iterable<S> entities);

    Optional<EventSeat> findById(Long id);

    @Query("""
            select es from EventSeat es
            join fetch es.seat s
            join fetch es.priceTier
            where es.event.id = :eventId
            order by s.section, s.rowLabel, s.id
            """)
    List<EventSeat> findSeatMap(Long eventId);
}
