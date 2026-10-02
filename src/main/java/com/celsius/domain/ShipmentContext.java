package com.celsius.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ShipmentContext(String origin, String destination, BigDecimal weightKg, BigDecimal declaredValue) {
    public static final List<String> CITY_ORDER = List.of("Bogotá", "Medellín", "Cali", "Barranquilla");
    public static final Set<String> CITIES = Set.copyOf(CITY_ORDER);
    public ShipmentContext {
        if (!CITIES.contains(origin == null ? "" : origin) || !CITIES.contains(destination == null ? "" : destination))
            throw new IllegalArgumentException("Selecciona ciudades del catálogo.");
        if (origin.equals(destination)) throw new IllegalArgumentException("El origen y el destino deben ser diferentes.");
        if (weightKg == null || weightKg.compareTo(new BigDecimal("0.1")) < 0 || weightKg.compareTo(new BigDecimal("25")) > 0)
            throw new IllegalArgumentException("El peso debe estar entre 0,1 y 25 kg.");
        if (weightKg.scale() > 2) throw new IllegalArgumentException("Usa como máximo dos decimales para el peso.");
        if (declaredValue == null || declaredValue.compareTo(new BigDecimal("10000")) < 0 || declaredValue.compareTo(new BigDecimal("20000000")) > 0)
            throw new IllegalArgumentException("El valor declarado debe estar entre $10.000 y $20.000.000 COP.");
        if (declaredValue.scale() > 2) throw new IllegalArgumentException("Usa como máximo dos decimales para el valor declarado.");
    }
}
