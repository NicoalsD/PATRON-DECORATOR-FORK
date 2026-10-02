package com.celsius.domain.decorator;

import com.celsius.domain.*;
import com.celsius.domain.profile.TemperatureSensor;

/** Añade el sensor que entrega la fábrica del perfil de carga (Abstract Factory). */
public final class TemperatureMonitorDecorator extends ShipmentDecorator {
    private final TemperatureSensor sensor;
    public TemperatureMonitorDecorator(Shipment wrapped, TemperatureSensor sensor) {
        super(wrapped); this.sensor = java.util.Objects.requireNonNull(sensor);
    }
    @Override public Quote quote() {
        Quote inner = super.quote();
        return inner.wrap(new QuoteLine("monitor", sensor.name(), name(), sensor.description(), sensor.fee()),
                sensor.capability(), inner.deliveryHours(), name(), "profile.createSensor()");
    }
}
