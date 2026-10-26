package io.github.shivibhardwaj.seatrush.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceTierRepo extends JpaRepository<PriceTier, Long> {
    List<PriceTier> findByEventIdOrderByPriceAsc(Long eventId);
}
