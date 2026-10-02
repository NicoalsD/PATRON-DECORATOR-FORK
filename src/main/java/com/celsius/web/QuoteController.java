package com.celsius.web;

import com.celsius.application.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class QuoteController {
    private final QuoteService service;
    public QuoteController(QuoteService service) { this.service = service; }
    @GetMapping("/services") public List<ServiceOption> catalog() { return QuoteService.CATALOG; }
    @GetMapping("/profiles") public List<ProfileOption> profiles() { return QuoteService.PROFILES; }
    @GetMapping(value = "/quotes/export", produces = "application/json")
    public ResponseEntity<QuoteResponse> export(@RequestParam String origin, @RequestParam String destination,
            @RequestParam BigDecimal weightKg, @RequestParam BigDecimal declaredValue,
            @RequestParam(required = false) List<String> services, @RequestParam(required = false) String profile) {
        QuoteResponse quote = service.calculate(new QuoteRequest(origin, destination, weightKg, declaredValue, services, profile));
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"celsius-cotizacion.json\"").body(quote);
    }
    @PostMapping("/quotes") public QuoteResponse quote(@RequestBody QuoteRequest request) { return service.calculate(request); }
}
