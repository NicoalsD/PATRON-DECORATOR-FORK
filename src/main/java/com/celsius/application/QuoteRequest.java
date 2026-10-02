package com.celsius.application;

import java.math.BigDecimal;
import java.util.List;

public record QuoteRequest(String origin, String destination, BigDecimal weightKg, BigDecimal declaredValue, List<String> services, String profile) {
    /** Sin perfil explícito se usa el refrigerado por defecto. */
    public QuoteRequest(String origin, String destination, BigDecimal weightKg, BigDecimal declaredValue, List<String> services) {
        this(origin, destination, weightKg, declaredValue, services, null);
    }
}
