package com.celsius.domain.decorator;

import com.celsius.domain.Quote;
import com.celsius.domain.Shipment;
import java.util.Objects;

/** Decorator: implementa Shipment, mantiene una referencia al componente que envuelve y delega en él por defecto. */
public abstract class ShipmentDecorator implements Shipment {
    protected final Shipment wrapped;
    protected ShipmentDecorator(Shipment wrapped) { this.wrapped = Objects.requireNonNull(wrapped); }

    /** Delegación por defecto: los decoradores concretos llaman a super.quote() y enriquecen el resultado. */
    @Override public Quote quote() { return wrapped.quote(); }

    /** Nombre que el decorador usa en el desglose y en la expresión de composición. */
    protected String name() { return getClass().getSimpleName(); }
}
