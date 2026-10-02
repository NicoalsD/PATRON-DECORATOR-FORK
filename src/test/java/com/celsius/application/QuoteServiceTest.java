package com.celsius.application;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class QuoteServiceTest {
    private final QuoteService service = new QuoteService();
    private QuoteRequest request(List<String> services) {
        return new QuoteRequest("Bogotá", "Medellín", new BigDecimal("2"), new BigDecimal("1500000"), services);
    }
    @Test void baseCanBeUsedWithoutDecorators() {
        var result = service.calculate(request(List.of()));
        assertThat(result.decorated().total()).isEqualByComparingTo("36400");
        assertThat(result.decorated().deliveryHours()).isEqualTo(48);
        assertThat(result.decorated().lines()).hasSize(1);
    }
    @Test void coldAndMonitoringAddTheirOwnCostsAndCapabilities() {
        var result = service.calculate(request(List.of("cold", "monitor")));
        assertThat(result.decorated().total()).isEqualByComparingTo("76000");
        assertThat(result.base().total()).isEqualByComparingTo("36400");
        assertThat(result.decorated().capabilities()).hasSize(3);
        assertThat(result.decorated().expression()).isEqualTo("new TemperatureMonitorDecorator(new ColdChainDecorator(new StandardShipment(context), profile.createPackaging(), context.weightKg()), profile.createSensor())");
    }
    @Test void priorityUsesTheInteriorSubtotalSoOrderChangesPrice() {
        var insuranceFirst = service.calculate(request(List.of("insurance", "priority"))).decorated();
        var priorityFirst = service.calculate(request(List.of("priority", "insurance"))).decorated();
        assertThat(insuranceFirst.total()).isEqualByComparingTo("65280");
        assertThat(priorityFirst.total()).isEqualByComparingTo("61680");
        assertThat(insuranceFirst.deliveryHours()).isEqualTo(36);
        assertThat(insuranceFirst.lines()).extracting("id").containsExactly("base", "insurance", "priority");
    }
    @Test void insuranceRespectsItsMinimum() {
        var req = new QuoteRequest("Bogotá", "Cali", BigDecimal.ONE, new BigDecimal("10000"), List.of("insurance"));
        assertThat(service.calculate(req).decorated().lines().get(1).amount()).isEqualByComparingTo("8000");
    }
    @Test void quoteCollectionsCannotBeMutatedByClients() {
        var quote = service.calculate(request(List.of("cold"))).decorated();
        assertThatThrownBy(() -> quote.lines().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> quote.capabilities().add("unvalidated")).isInstanceOf(UnsupportedOperationException.class);
    }
    @Test void invalidRoutesWeightsAndServicesAreRejected() {
        assertThatThrownBy(() -> service.calculate(new QuoteRequest("Bogotá", "Bogotá", BigDecimal.ONE, new BigDecimal("10000"), List.of())))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("diferentes");
        assertThatThrownBy(() -> service.calculate(new QuoteRequest("Bogotá", "Cali", new BigDecimal("26"), new BigDecimal("10000"), List.of())))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("peso");
        assertThatThrownBy(() -> service.calculate(request(List.of("cold", "cold")))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.calculate(request(List.of("unknown")))).isInstanceOf(IllegalArgumentException.class);
    }
}
