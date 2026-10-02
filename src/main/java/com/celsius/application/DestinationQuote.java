package com.celsius.application;

import java.math.BigDecimal;

/** Una fila de la comparación de destinos. */
public record DestinationQuote(String destination, BigDecimal total, int deliveryHours, boolean current) {}
