package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct A3: empaque aislado para temperatura ambiente controlada de 15–25 °C. */
public record ControlledRoomPackaging() implements Packaging {
    public String name() { return "Empaque de ambiente controlado"; }
    public String description() { return "Aislamiento pasivo: $9.000 + $600 por kg"; }
    public String rate() { return "$9.000 + $600/kg"; }
    public String capability() { return "Empaque aislado de ejemplo: 15–25 °C"; }
    public BigDecimal fixedFee() { return new BigDecimal("9000"); }
    public BigDecimal feePerKg() { return new BigDecimal("600"); }
}
