package com.celsius.domain.profile;

/** ConcreteFactory 3: medicamentos estables a temperatura ambiente controlada. */
public final class ControlledRoomProfileFactory implements CargoProfileFactory {
    public String id() { return "ambient"; }
    public String name() { return "Ambiente controlado"; }
    public String temperatureRange() { return "15–25 °C"; }
    public Packaging createPackaging() { return new ControlledRoomPackaging(); }
    public TemperatureSensor createSensor() { return new ExposureIndicatorSensor(); }
}
