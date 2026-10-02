package com.celsius.domain.decorator;

import com.celsius.domain.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** El recargo se aplica al subtotal interior: mover este wrapper cambia el resultado. */
public final class PriorityDecorator extends ShipmentDecorator {
    public PriorityDecorator(Shipment wrapped) { super(wrapped); }
    @Override public Quote quote() {
        Quote inner = super.quote();
        BigDecimal fee = inner.total().multiply(new BigDecimal("0.20")).setScale(0, RoundingMode.HALF_UP);
        return inner.wrap(new QuoteLine("priority", "Manejo prioritario", name(), "20 % del subtotal interior: $" + inner.total().toPlainString(), fee),
                "Atención prioritaria: 12 horas menos", Math.max(24, inner.deliveryHours() - 12), name());
    }
}
