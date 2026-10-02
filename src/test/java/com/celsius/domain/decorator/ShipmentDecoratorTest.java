package com.celsius.domain.decorator;

import com.celsius.domain.*;
import com.celsius.domain.profile.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Verifica las propiedades del patrón directamente sobre el dominio, sin pasar por QuoteService. */
class ShipmentDecoratorTest {
    private final ShipmentContext context = new ShipmentContext("Bogotá", "Medellín", new BigDecimal("2"), new BigDecimal("1500000"));
    private final Shipment base = new StandardShipment(context);

    @Test void decoratorIsInterchangeableWithTheComponent() {
        Shipment decorated = new CustodyDecorator(base);
        assertThat(decorated).isInstanceOf(Shipment.class).isInstanceOf(ShipmentDecorator.class);
    }
    @Test void abstractDecoratorDelegatesToTheWrappedComponentByDefault() {
        Shipment transparent = new ShipmentDecorator(base) {};
        assertThat(transparent.quote()).isEqualTo(base.quote());
    }
    @Test void decoratorRequiresAComponentToWrap() {
        assertThatThrownBy(() -> new CustodyDecorator(null)).isInstanceOf(NullPointerException.class);
    }
    @Test void decoratingDoesNotAlterTheWrappedComponent() {
        Quote before = base.quote();
        new PriorityDecorator(new InsuranceDecorator(base, context.declaredValue())).quote();
        assertThat(base.quote()).isEqualTo(before);
    }
    @Test void eachDecoratorAddsExactlyItsOwnLineOnTopOfTheInteriorQuote() {
        Quote inner = base.quote();
        Quote outer = new TemperatureMonitorDecorator(base, new DataLoggerSensor()).quote();
        assertThat(outer.lines()).hasSize(inner.lines().size() + 1).startsWith(inner.lines().toArray(QuoteLine[]::new));
        assertThat(outer.total()).isEqualByComparingTo(inner.total().add(new BigDecimal("12000")));
        assertThat(outer.lines().getLast().className()).isEqualTo("TemperatureMonitorDecorator");
    }
    @Test void decoratorsCanWrapOtherDecoratorsRecursively() {
        Shipment chain = new CustodyDecorator(new TemperatureMonitorDecorator(new ColdChainDecorator(base, new RefrigeratedPackaging(), context.weightKg()), new DataLoggerSensor()));
        assertThat(chain.quote().lines()).extracting(QuoteLine::id).containsExactly("base", "cold", "monitor", "custody");
        assertThat(chain.quote().expression())
            .isEqualTo("new CustodyDecorator(new TemperatureMonitorDecorator(new ColdChainDecorator(new StandardShipment(context), profile.createPackaging(), context.weightKg()), profile.createSensor()))");
    }
    @Test void priorityReducesDeliveryTimeButNeverBelowItsFloor() {
        Shipment twice = new PriorityDecorator(new PriorityDecorator(new PriorityDecorator(base)));
        assertThat(new PriorityDecorator(base).quote().deliveryHours()).isEqualTo(36);
        assertThat(twice.quote().deliveryHours()).isEqualTo(24);
    }
}
