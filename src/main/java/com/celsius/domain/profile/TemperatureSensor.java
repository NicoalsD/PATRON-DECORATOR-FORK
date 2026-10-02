package com.celsius.domain.profile;

import java.math.BigDecimal;

/** AbstractProduct B: dispositivo que registra la temperatura durante el trayecto. */
public interface TemperatureSensor {
    String name();
    String description();
    String rate();
    String capability();
    BigDecimal fee();
}
