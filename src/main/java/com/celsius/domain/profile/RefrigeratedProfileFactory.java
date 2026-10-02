package com.celsius.domain.profile;

/** ConcreteFactory 1: vacunas, insulina y biológicos que viajan refrigerados. */
public final class RefrigeratedProfileFactory implements CargoProfileFactory {
    public String id() { return "refrigerated"; }
    public String name() { return "Refrigerado"; }
    public String temperatureRange() { return "2–8 °C"; }
    public Packaging createPackaging() { return new RefrigeratedPackaging(); }
    public TemperatureSensor createSensor() { return new DataLoggerSensor(); }
}
