package com.celsius.domain.builder;

import com.celsius.domain.*;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ShipmentBuilderTest {
    private final ShipmentContext context = new ShipmentContext("Bogotá", "Medellín", new BigDecimal("2"), new BigDecimal("1500000"));

    @Test void withoutStepsTheProductIsTheBaseShipment() {
        assertThat(new ShipmentBuilder(context).build()).isInstanceOf(StandardShipment.class);
    }
    @Test void stepsAreAppliedInTheOrderTheyAreCalled() {
        Quote quote = new ShipmentBuilder(context).insurance().priority().build().quote();
        assertThat(quote.lines()).extracting(QuoteLine::id).containsExactly("base", "insurance", "priority");
        assertThat(quote.total()).isEqualByComparingTo("65280");
    }
    @Test void fluentStepsAndCatalogIdsBuildTheSameProduct() {
        Quote fluent = new ShipmentBuilder(context).coldChain().temperatureMonitor().build().quote();
        Shipment byIds = ShipmentDirector.construct(new ShipmentBuilder(context), List.of("cold", "monitor"));
        assertThat(byIds.quote()).isEqualTo(fluent);
    }
    @Test void directorRecipeBuildsTheVaccinePackage() {
        Quote quote = ShipmentDirector.vaccines(new ShipmentBuilder(context)).quote();
        assertThat(quote.lines()).extracting(QuoteLine::id).containsExactly("base", "cold", "monitor", "custody");
    }
    @Test void builderRejectsDuplicatedAndUnknownSteps() {
        assertThatThrownBy(() -> new ShipmentBuilder(context).custody().custody()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("una sola vez");
        assertThatThrownBy(() -> new ShipmentBuilder(context).service("unknown")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ShipmentBuilder(context).service(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
