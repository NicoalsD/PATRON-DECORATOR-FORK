package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct A2: empaque con hielo seco de −25 a −15 °C. */
public record FrozenPackaging() implements Packaging {
    public String name() { return "Empaque congelado"; }
    public String description() { return "Hielo seco: $38.000 + $3.500 por kg"; }
    public String rate() { return "$38.000 + $3.500/kg"; }
    public String capability() { return "Empaque con hielo seco de ejemplo: −25 a −15 °C"; }
    public BigDecimal fixedFee() { return new BigDecimal("38000"); }
    public BigDecimal feePerKg() { return new BigDecimal("3500"); }
}
