package com.celsius.domain.profile;

import java.util.List;

/** Catálogo de fábricas concretas. El cliente elige una por id y trabaja solo con CargoProfileFactory. */
public final class CargoProfiles {
    public static final CargoProfileFactory DEFAULT = new RefrigeratedProfileFactory();
    public static final List<CargoProfileFactory> ALL = List.of(DEFAULT, new FrozenProfileFactory(), new ControlledRoomProfileFactory());

    private CargoProfiles() {}

    /** Sin perfil se usa el refrigerado, que conserva las tarifas originales del simulador. */
    public static CargoProfileFactory byId(String id) {
        if (id == null || id.isBlank()) return DEFAULT;
        return ALL.stream().filter(profile -> profile.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Perfil de carga no reconocido: " + id));
    }
}
