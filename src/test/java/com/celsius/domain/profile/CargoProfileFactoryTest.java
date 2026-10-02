package com.celsius.domain.profile;

import com.celsius.application.QuoteRequest;
import com.celsius.application.QuoteService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CargoProfileFactoryTest {
    private final QuoteService service = new QuoteService();
    private QuoteRequest request(String profile) {
        return new QuoteRequest("Bogotá", "Medellín", new BigDecimal("2"), new BigDecimal("1500000"), List.of("cold", "monitor"), profile);
    }

    @Test void eachFactoryCreatesAConsistentFamily() {
        assertThat(new RefrigeratedProfileFactory().createPackaging()).isInstanceOf(RefrigeratedPackaging.class);
        assertThat(new RefrigeratedProfileFactory().createSensor()).isInstanceOf(DataLoggerSensor.class);
        assertThat(new FrozenProfileFactory().createPackaging()).isInstanceOf(FrozenPackaging.class);
        assertThat(new FrozenProfileFactory().createSensor()).isInstanceOf(CryogenicSensor.class);
        assertThat(new ControlledRoomProfileFactory().createPackaging()).isInstanceOf(ControlledRoomPackaging.class);
        assertThat(new ControlledRoomProfileFactory().createSensor()).isInstanceOf(ExposureIndicatorSensor.class);
    }
    @Test void refrigeratedIsTheDefaultAndKeepsTheOriginalRates() {
        assertThat(CargoProfiles.byId(null)).isSameAs(CargoProfiles.DEFAULT).isInstanceOf(RefrigeratedProfileFactory.class);
        assertThat(service.calculate(request(null)).decorated().total()).isEqualByComparingTo("76000");
        assertThat(service.calculate(request("refrigerated")).profile().id()).isEqualTo("refrigerated");
    }
    @Test void theChosenFactoryChangesPackagingAndSensorTogether() {
        var frozen = service.calculate(request("frozen")).decorated();
        assertThat(frozen.total()).isEqualByComparingTo("101400");
        assertThat(frozen.capabilities()).contains("Empaque con hielo seco de ejemplo: −25 a −15 °C", "Registro continuo bajo −15 °C con alarma");
        var ambient = service.calculate(request("ambient")).decorated();
        assertThat(ambient.total()).isEqualByComparingTo("53600");
        assertThat(ambient.lines()).extracting("id").containsExactly("base", "cold", "monitor");
    }
    @Test void unknownProfileIsRejected() {
        assertThatThrownBy(() -> service.calculate(request("tropical"))).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Perfil");
    }
}
