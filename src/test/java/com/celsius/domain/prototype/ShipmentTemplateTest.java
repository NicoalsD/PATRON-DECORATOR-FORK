package com.celsius.domain.prototype;

import com.celsius.application.QuoteRequest;
import com.celsius.application.QuoteService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ShipmentTemplateTest {
    private final ShipmentTemplate original = new ShipmentTemplate("t", "Prueba", "Bogotá", "Medellín",
            new BigDecimal("2"), new BigDecimal("1500000"), "refrigerated", List.of("cold"));

    @Test void copyIsADifferentObjectWithTheSameState() {
        ShipmentTemplate clone = original.copy();
        assertThat(clone).isNotSameAs(original);
        assertThat(clone).usingRecursiveComparison().isEqualTo(original);
    }
    @Test void changingACloneDoesNotAlterTheOriginal() {
        original.copy().destination("Cali").profile("frozen").addService("priority");
        assertThat(original.destination()).isEqualTo("Medellín");
        assertThat(original.profile()).isEqualTo("refrigerated");
        assertThat(original.services()).containsExactly("cold");
    }
    @Test void registryHandsOutCopiesAndKeepsItsPrototypesIntact() {
        TemplateRegistry registry = TemplateRegistry.withDefaults();
        ShipmentTemplate first = registry.get("vaccines");
        first.destination("Barranquilla").addService("insurance");
        ShipmentTemplate second = registry.get("vaccines");
        assertThat(second).isNotSameAs(first);
        assertThat(second.destination()).isEqualTo("Medellín");
        assertThat(second.services()).containsExactly("cold", "monitor");
        assertThat(registry.all()).extracting(ShipmentTemplate::id).containsExactly("vaccines", "lab", "base");
        assertThatThrownBy(() -> registry.get("missing")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void compareDestinationsQuotesOneCloneForEachOtherCity() {
        var request = new QuoteRequest("Bogotá", "Medellín", new BigDecimal("2"), new BigDecimal("1500000"), List.of("cold", "monitor"), null);
        var rows = new QuoteService().compareDestinations(request);
        assertThat(rows).extracting("destination").containsExactly("Medellín", "Cali", "Barranquilla");
        assertThat(rows.get(0).current()).isTrue();
        assertThat(rows.get(0).total()).isEqualByComparingTo("76000");
        assertThat(rows.get(2).total()).isEqualByComparingTo("90000");
        assertThat(rows.get(2).deliveryHours()).isEqualTo(72);
    }
}
