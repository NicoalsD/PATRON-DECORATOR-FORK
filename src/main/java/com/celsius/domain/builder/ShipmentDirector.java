package com.celsius.domain.builder;

import com.celsius.domain.Shipment;
import java.util.List;

/** Director: conoce el orden de construcción y dirige al ShipmentBuilder; no sabe qué decoradores existen. */
public final class ShipmentDirector {
    private ShipmentDirector() {}

    /** Construye el envío con los servicios en el orden elegido por el usuario (de adentro hacia afuera). */
    public static Shipment construct(ShipmentBuilder builder, List<String> services) {
        for (String id : services == null ? List.<String>of() : services) builder.service(id);
        return builder.build();
    }

    /** Receta predefinida: envío de vacunas con frío, registro de temperatura y cadena de custodia. */
    public static Shipment vaccines(ShipmentBuilder builder) {
        return builder.coldChain().temperatureMonitor().custody().build();
    }
}
