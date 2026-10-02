package com.celsius.application;

import com.celsius.domain.profile.CargoProfileFactory;

/** Vista del perfil de carga para la API y la plantilla; las tarifas salen de los productos de la fábrica. */
public record ProfileOption(String id, String name, String temperatureRange, String packagingRate, String sensorRate) {
    public static ProfileOption of(CargoProfileFactory factory) {
        return new ProfileOption(factory.id(), factory.name(), factory.temperatureRange(),
                factory.createPackaging().rate(), factory.createSensor().rate());
    }
}
