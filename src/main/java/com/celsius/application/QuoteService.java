package com.celsius.application;

import com.celsius.domain.*;
import com.celsius.domain.builder.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuoteService {
    public static final List<ServiceOption> CATALOG = List.of(
        new ServiceOption("cold", "Empaque refrigerado", "ColdChainDecorator", "Añade aislamiento y material refrigerante al envío.", "$24.000 + $1.800/kg", "FRÍO"),
        new ServiceOption("monitor", "Registro de temperatura", "TemperatureMonitorDecorator", "Añade un registrador de temperatura al trayecto.", "$12.000 por envío", "TEMP"),
        new ServiceOption("custody", "Cadena de custodia", "CustodyDecorator", "Añade actas de entrega y un sello de integridad.", "$18.000 por envío", "ACTA"),
        new ServiceOption("insurance", "Seguro de carga", "InsuranceDecorator", "Añade una cobertura calculada sobre el valor declarado.", "1,2 % · mínimo $8.000", "SEGURO"),
        new ServiceOption("priority", "Manejo prioritario", "PriorityDecorator", "Añade un recargo sobre el subtotal que recibe y reduce 12 h.", "20 % del subtotal interior", "PRIO")
    );

    public QuoteResponse calculate(QuoteRequest request) {
        if (request == null) throw new IllegalArgumentException("Completa los datos del envío.");
        ShipmentContext context = new ShipmentContext(request.origin(), request.destination(), request.weightKg(), request.declaredValue());
        List<String> selected = request.services() == null ? List.of() : request.services();
        // Director + Builder: cada servicio elegido envuelve al envío anterior, en el orden recibido.
        Shipment shipment = ShipmentDirector.construct(new ShipmentBuilder(context), selected);
        return new QuoteResponse(context, new StandardShipment(context).quote(), shipment.quote(), "Tarifas, plazos y coberturas simulados para un taller académico.");
    }
}
