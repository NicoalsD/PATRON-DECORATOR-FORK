package com.celsius.domain.decorator;

import com.celsius.domain.*;
import com.celsius.domain.profile.Packaging;
import java.math.BigDecimal;

/** Añade el empaque térmico que entrega la fábrica del perfil de carga (Abstract Factory). */
public final class ColdChainDecorator extends ShipmentDecorator {
    private final Packaging packaging;
    private final BigDecimal weightKg;
    public ColdChainDecorator(Shipment wrapped, Packaging packaging, BigDecimal weightKg) {
        super(wrapped); this.packaging = java.util.Objects.requireNonNull(packaging); this.weightKg = weightKg;
    }
    @Override public Quote quote() {
        Quote inner = super.quote();
        return inner.wrap(new QuoteLine("cold", packaging.name(), name(), packaging.description(), packaging.fee(weightKg)),
                packaging.capability(), inner.deliveryHours(), name(), "profile.createPackaging()", "context.weightKg()");
    }
}
