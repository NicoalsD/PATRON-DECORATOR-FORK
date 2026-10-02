package com.celsius.domain.builder;

import com.celsius.domain.*;
import com.celsius.domain.decorator.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Builder (ConcreteBuilder): arma paso a paso el producto Shipment. Parte del StandardShipment y cada paso
 * envuelve el resultado anterior con un decorador, en el orden en que se invoca. build() entrega el producto.
 */
public final class ShipmentBuilder {
    private final ShipmentContext context;
    private final Set<String> added = new HashSet<>();
    private Shipment shipment;

    public ShipmentBuilder(ShipmentContext context) {
        this.context = Objects.requireNonNull(context);
        this.shipment = new StandardShipment(context);
    }

    public ShipmentBuilder coldChain() { return layer("cold", inner -> new ColdChainDecorator(inner, context.weightKg())); }
    public ShipmentBuilder temperatureMonitor() { return layer("monitor", TemperatureMonitorDecorator::new); }
    public ShipmentBuilder custody() { return layer("custody", CustodyDecorator::new); }
    public ShipmentBuilder insurance() { return layer("insurance", inner -> new InsuranceDecorator(inner, context.declaredValue())); }
    public ShipmentBuilder priority() { return layer("priority", PriorityDecorator::new); }

    /** Paso genérico por identificador del catálogo; lo usa el Director para traducir la selección del usuario. */
    public ShipmentBuilder service(String id) {
        if (id == null) throw new IllegalArgumentException("Servicio no reconocido.");
        return switch (id) {
            case "cold" -> coldChain();
            case "monitor" -> temperatureMonitor();
            case "custody" -> custody();
            case "insurance" -> insurance();
            case "priority" -> priority();
            default -> throw new IllegalArgumentException("Servicio no reconocido: " + id);
        };
    }

    public Shipment build() { return shipment; }

    private ShipmentBuilder layer(String id, UnaryOperator<Shipment> decorator) {
        if (!added.add(id)) throw new IllegalArgumentException("Cada servicio se puede añadir una sola vez.");
        shipment = decorator.apply(shipment);
        return this;
    }
}
