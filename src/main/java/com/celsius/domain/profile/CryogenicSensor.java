package com.celsius.domain.profile;

import java.math.BigDecimal;

/** ConcreteProduct B2: sensor de baja temperatura con alarma para carga congelada. */
public record CryogenicSensor() implements TemperatureSensor {
    public String name() { return "Registro de temperatura"; }
    public String description() { return "Sensor de baja temperatura con alarma"; }
    public String rate() { return "$20.000 por envío"; }
    public String capability() { return "Registro continuo bajo −15 °C con alarma"; }
    public BigDecimal fee() { return new BigDecimal("20000"); }
}
