package com.celsius.web;

import com.celsius.application.QuoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    private final QuoteService service;
    public PageController(QuoteService service) { this.service = service; }

    @GetMapping("/") public String index(Model model) {
        model.addAttribute("services", QuoteService.CATALOG);
        model.addAttribute("profiles", QuoteService.PROFILES);
        model.addAttribute("templates", service.templates());
        return "index";
    }
}
