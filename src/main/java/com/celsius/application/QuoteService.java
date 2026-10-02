package com.celsius.application;

import com.celsius.domain.*;
import com.celsius.domain.builder.*;
import com.celsius.domain.profile.CargoProfileFactory;
import com.celsius.domain.profile.CargoProfiles;
import com.celsius.domain.prototype.ShipmentTemplate;
import com.celsius.domain.prototype.TemplateRegistry;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuoteService {
    public static final List<ServiceOption> CATALOG = List.of(
        new ServiceOption("cold", "Empaque térmico", "ColdChainDecorator", "Añade el empaque que corresponde al perfil térmico elegido.", "$24.000 + $1.800/kg", "FRÍO"),
        new ServiceOption("monitor", "Registro de temperatura", "TemperatureMonitorDecorator", "Añade el sensor de temperatura compatible con el perfil.", "$12.000 por envío", "TEMP"),
        new ServiceOption("custody", "Cadena de custodia", "CustodyDecorator", "Añade actas de entrega y un sello de integridad.", "$18.000 por envío", "ACTA"),
        new ServiceOption("insurance", "Seguro de carga", "InsuranceDecorator", "Añade una cobertura calculada sobre el valor declarado.", "1,2 % · mínimo $8.000", "SEGURO"),
        new ServiceOption("priority", "Manejo prioritario", "PriorityDecorator", "Añade un recargo sobre el subtotal que recibe y reduce 12 h.", "20 % del subtotal interior", "PRIO")
    );
    private final TemplateRegistry templates = TemplateRegistry.withDefaults();
    public static final List<ProfileOption> PROFILES = CargoProfiles.ALL.stream().map(ProfileOption::of).toList();

    public QuoteResponse calculate(QuoteRequest request) {
        if (request == null) throw new IllegalArgumentException("Completa los datos del envío.");
        ShipmentContext context = new ShipmentContext(request.origin(), request.destination(), request.weightKg(), request.declaredValue());
        CargoProfileFactory profile = CargoProfiles.byId(request.profile());
        List<String> selected = request.services() == null ? List.of() : request.services();
        // Director + Builder: cada servicio elegido envuelve al envío anterior, en el orden recibido.
        Shipment shipment = ShipmentDirector.construct(new ShipmentBuilder(context, profile), selected);
        return new QuoteResponse(context, ProfileOption.of(profile), new StandardShipment(context).quote(), shipment.quote(), "Tarifas, plazos y coberturas simulados para un taller académico.");
    }

    /** Escenarios de ejemplo: el registro entrega copias de sus prototipos. */
    public List<TemplateOption> templates() { return templates.all().stream().map(TemplateOption::of).toList(); }

    /**
     * Comparar destinos: clona la configuración actual (Prototype) una vez por ciudad, cambia solo el destino
     * y cotiza cada copia. El prototipo original no se modifica.
     */
    public List<DestinationQuote> compareDestinations(QuoteRequest request) {
        if (request == null) throw new IllegalArgumentException("Completa los datos del envío.");
        calculate(request);
        var prototype = new ShipmentTemplate("current", "Envío actual", request.origin(), request.destination(),
                request.weightKg(), request.declaredValue(), request.profile(), request.services());
        return ShipmentContext.CITY_ORDER.stream().filter(city -> !city.equals(prototype.origin()))
                .map(city -> {
                    ShipmentTemplate clone = prototype.copy().destination(city);
                    Quote quote = calculate(toRequest(clone)).decorated();
                    return new DestinationQuote(city, quote.total(), quote.deliveryHours(), city.equals(prototype.destination()));
                }).toList();
    }

    private static QuoteRequest toRequest(ShipmentTemplate template) {
        return new QuoteRequest(template.origin(), template.destination(), template.weightKg(), template.declaredValue(),
                template.services(), template.profile());
    }
}
