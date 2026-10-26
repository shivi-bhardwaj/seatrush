package io.github.shivibhardwaj.seatrush.event;

import io.github.shivibhardwaj.seatrush.common.BusinessRuleException;
import io.github.shivibhardwaj.seatrush.common.ConflictException;
import io.github.shivibhardwaj.seatrush.common.ConstraintViolations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PriceTierService {

    private final PriceTierRepo priceTierRepo;
    private final EventService eventService;

    public PriceTierService(PriceTierRepo priceTierRepo, EventService eventService) {
        this.priceTierRepo = priceTierRepo;
        this.eventService = eventService;
    }

    @Transactional
    public PriceTier createPriceTier(String name, BigDecimal price, Long eventId) {
        Event event = eventService.getEvent(eventId);
        if (!event.canAddPriceTier()) {
            throw new BusinessRuleException("Event " + eventId + " cannot add price tier");
        }

        try {
            return priceTierRepo.save(new PriceTier(name, price, event));
        } catch (DataIntegrityViolationException ex) {
            if (ConstraintViolations.isViolation(ex, "uk_price_tier_event_name")) {
                throw new ConflictException("Duplicate Event PriceTier name not allowed", ex);
            }
            throw ex;
        }
    }

    public List<PriceTier> getPriceTiers(Long eventId){
        eventService.getEvent(eventId);
        return priceTierRepo.findByEventIdOrderByPriceAsc(eventId);
    }
}
