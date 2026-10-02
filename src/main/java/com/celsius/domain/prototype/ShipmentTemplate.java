package com.celsius.domain.prototype;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ConcretePrototype: configuración de envío reutilizable (ruta, peso, valor, perfil y servicios).
 * Es mutable a propósito; copy() hace una copia profunda para que ajustar un clon no altere el original.
 */
public final class ShipmentTemplate implements Prototype<ShipmentTemplate> {
    private final String id;
    private final String name;
    private String origin;
    private String destination;
    private BigDecimal weightKg;
    private BigDecimal declaredValue;
    private String profile;
    private final List<String> services;

    public ShipmentTemplate(String id, String name, String origin, String destination, BigDecimal weightKg,
                            BigDecimal declaredValue, String profile, List<String> services) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.origin = origin;
        this.destination = destination;
        this.weightKg = weightKg;
        this.declaredValue = declaredValue;
        this.profile = profile;
        this.services = new ArrayList<>(services == null ? List.of() : services);
    }

    private ShipmentTemplate(ShipmentTemplate source) {
        this(source.id, source.name, source.origin, source.destination, source.weightKg, source.declaredValue, source.profile, source.services);
    }

    @Override public ShipmentTemplate copy() { return new ShipmentTemplate(this); }

    public ShipmentTemplate origin(String origin) { this.origin = origin; return this; }
    public ShipmentTemplate destination(String destination) { this.destination = destination; return this; }
    public ShipmentTemplate weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
    public ShipmentTemplate declaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; return this; }
    public ShipmentTemplate profile(String profile) { this.profile = profile; return this; }
    public ShipmentTemplate addService(String service) { services.add(service); return this; }

    public String id() { return id; }
    public String name() { return name; }
    public String origin() { return origin; }
    public String destination() { return destination; }
    public BigDecimal weightKg() { return weightKg; }
    public BigDecimal declaredValue() { return declaredValue; }
    public String profile() { return profile; }
    public List<String> services() { return List.copyOf(services); }
}
