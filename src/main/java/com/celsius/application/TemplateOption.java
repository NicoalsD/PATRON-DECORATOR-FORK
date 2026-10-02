package com.celsius.application;

import com.celsius.domain.prototype.ShipmentTemplate;
import java.math.BigDecimal;
import java.util.List;

/** Vista de un escenario para la API y la plantilla Thymeleaf. */
public record TemplateOption(String id, String name, String origin, String destination, BigDecimal weightKg,
                             BigDecimal declaredValue, String profile, List<String> services) {
    public static TemplateOption of(ShipmentTemplate template) {
        return new TemplateOption(template.id(), template.name(), template.origin(), template.destination(),
                template.weightKg(), template.declaredValue(), template.profile(), template.services());
    }
    public String servicesCsv() { return String.join(",", services); }
}
