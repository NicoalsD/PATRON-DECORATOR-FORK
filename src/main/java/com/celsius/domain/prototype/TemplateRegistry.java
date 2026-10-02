package com.celsius.domain.prototype;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registro de prototipos: guarda los originales y siempre entrega copias, nunca la instancia registrada. */
public final class TemplateRegistry {
    private final Map<String, ShipmentTemplate> prototypes = new LinkedHashMap<>();

    public void register(ShipmentTemplate prototype) { prototypes.put(prototype.id(), prototype.copy()); }

    public ShipmentTemplate get(String id) {
        ShipmentTemplate prototype = prototypes.get(id);
        if (prototype == null) throw new IllegalArgumentException("Escenario no reconocido: " + id);
        return prototype.copy();
    }

    public List<ShipmentTemplate> all() { return prototypes.values().stream().map(ShipmentTemplate::copy).toList(); }

    /** Escenarios de ejemplo del cotizador. */
    public static TemplateRegistry withDefaults() {
        var registry = new TemplateRegistry();
        registry.register(new ShipmentTemplate("vaccines", "Vacunas en tránsito", "Bogotá", "Medellín",
                new BigDecimal("2"), new BigDecimal("1500000"), "refrigerated", List.of("cold", "monitor")));
        registry.register(new ShipmentTemplate("lab", "Muestra de laboratorio", "Cali", "Barranquilla",
                new BigDecimal("1.5"), new BigDecimal("2500000"), "frozen", List.of("cold", "monitor", "custody", "insurance", "priority")));
        registry.register(new ShipmentTemplate("base", "Solo transporte", "Bogotá", "Cali",
                new BigDecimal("3"), new BigDecimal("500000"), "refrigerated", List.of()));
        return registry;
    }
}
