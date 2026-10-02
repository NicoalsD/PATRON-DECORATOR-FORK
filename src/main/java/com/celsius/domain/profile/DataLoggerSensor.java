package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct B1: registrador de datos para carga refrigerada. */
public record DataLoggerSensor() implements TemperatureSensor {
    public String name() { return "Registro de temperatura"; }
    public String description() { return "Registrador de datos por envío"; }
    public String rate() { return "$12.000 por envío"; }
    public String capability() { return "Registro de temperatura durante el trayecto"; }
    public BigDecimal fee() { return new BigDecimal("12000"); }
}
