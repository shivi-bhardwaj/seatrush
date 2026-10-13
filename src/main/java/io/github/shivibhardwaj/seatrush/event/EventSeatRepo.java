package io.github.shivibhardwaj.seatrush.event;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface EventSeatRepo extends Repository<EventSeat, Long> {

    <S extends EventSeat> S save(S entity);

    <S extends EventSeat> List<S> saveAll(Iterable<S> entities);

    Optional<EventSeat> findById(Long id);
}
