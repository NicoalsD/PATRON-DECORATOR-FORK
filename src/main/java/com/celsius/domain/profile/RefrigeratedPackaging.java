package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct A1: empaque refrigerado de 2–8 °C. */
public record RefrigeratedPackaging() implements Packaging {
    public String name() { return "Empaque refrigerado"; }
    public String description() { return "$24.000 + $1.800 por kg"; }
    public String rate() { return "$24.000 + $1.800/kg"; }
    public String capability() { return "Empaque térmico de ejemplo: 2–8 °C"; }
    public BigDecimal fixedFee() { return new BigDecimal("24000"); }
    public BigDecimal feePerKg() { return new BigDecimal("1800"); }
}
