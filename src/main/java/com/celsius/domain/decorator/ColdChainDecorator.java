package com.celsius.domain.decorator;

import com.celsius.domain.*;
import java.math.BigDecimal;

public final class ColdChainDecorator extends ShipmentDecorator {
    private final BigDecimal weightKg;
    public ColdChainDecorator(Shipment wrapped, BigDecimal weightKg) { super(wrapped); this.weightKg = weightKg; }
    @Override public Quote quote() {
        Quote inner = super.quote();
        BigDecimal fee = new BigDecimal("24000").add(weightKg.multiply(new BigDecimal("1800"))).setScale(0, java.math.RoundingMode.HALF_UP);
        return inner.wrap(new QuoteLine("cold", "Empaque refrigerado", name(), "$24.000 + $1.800 por kg", fee),
                "Empaque térmico de ejemplo: 2–8 °C", inner.deliveryHours(), name(), "context.weightKg()");
    }
}
