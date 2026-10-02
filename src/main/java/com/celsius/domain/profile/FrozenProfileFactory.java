package com.celsius.domain.profile;

/** ConcreteFactory 2: biológicos y muestras que deben viajar congelados. */
public final class FrozenProfileFactory implements CargoProfileFactory {
    public String id() { return "frozen"; }
    public String name() { return "Congelado"; }
    public String temperatureRange() { return "−25 a −15 °C"; }
    public Packaging createPackaging() { return new FrozenPackaging(); }
    public TemperatureSensor createSensor() { return new CryogenicSensor(); }
}
