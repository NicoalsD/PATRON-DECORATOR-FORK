package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct B3: indicador de exposición térmica para ambiente controlado. */
public record ExposureIndicatorSensor() implements TemperatureSensor {
    public String name() { return "Registro de temperatura"; }
    public String description() { return "Indicador de exposición térmica"; }
    public String rate() { return "$7.000 por envío"; }
    public String capability() { return "Indicador de exposición fuera de 15–25 °C"; }
    public BigDecimal fee() { return new BigDecimal("7000"); }
}
