package io.github.shivibhardwaj.seatrush.event;

import java.math.BigDecimal;
import java.util.Currency;

public record PriceTierResponse(Long id, String name, BigDecimal price,
                                Long eventId, Currency currency) {
    static PriceTierResponse from(PriceTier priceTier){
        return new PriceTierResponse(priceTier.getId(), priceTier.getName(), priceTier.getPrice(),
                priceTier.getEvent().getId(), priceTier.getCurrency());
    }
}
