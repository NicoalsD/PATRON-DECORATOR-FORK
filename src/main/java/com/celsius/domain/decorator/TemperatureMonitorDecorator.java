package com.celsius.domain.decorator;

import com.celsius.domain.*;
import java.math.BigDecimal;

public final class TemperatureMonitorDecorator extends ShipmentDecorator {
    public TemperatureMonitorDecorator(Shipment wrapped) { super(wrapped); }
    @Override public Quote quote() {
        Quote inner = super.quote();
        return inner.wrap(new QuoteLine("monitor", "Registro de temperatura", name(), "Registrador de datos por envío", new BigDecimal("12000")),
                "Registro de temperatura durante el trayecto", inner.deliveryHours(), name());
    }
}
