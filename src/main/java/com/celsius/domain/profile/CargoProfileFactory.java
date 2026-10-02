package com.celsius.domain.profile;

/**
 * Abstract Factory: crea una familia de productos compatibles para un perfil térmico de carga.
 * Un empaque congelado siempre viaja con un sensor de baja temperatura, nunca con el de ambiente.
 */
public interface CargoProfileFactory {
    String id();
    String name();
    String temperatureRange();
    Packaging createPackaging();
    TemperatureSensor createSensor();
}
