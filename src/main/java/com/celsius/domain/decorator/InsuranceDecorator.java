package com.celsius.domain.decorator;

import com.celsius.domain.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class InsuranceDecorator extends ShipmentDecorator {
    private final BigDecimal declaredValue;
    public InsuranceDecorator(Shipment wrapped, BigDecimal declaredValue) { super(wrapped); this.declaredValue = declaredValue; }
    @Override public Quote quote() {
        Quote inner = super.quote();
        BigDecimal fee = declaredValue.multiply(new BigDecimal("0.012")).max(new BigDecimal("8000")).setScale(0, RoundingMode.HALF_UP);
        return inner.wrap(new QuoteLine("insurance", "Seguro de carga", name(), "1,2 % del valor declarado; mínimo $8.000", fee),
                "Cobertura ilustrativa del valor declarado", inner.deliveryHours(), name(), "context.declaredValue()");
    }
}
