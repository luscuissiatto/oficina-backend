package br.com.fiap.oficina.application.dto;

import java.math.BigDecimal;

public record ServiceRequestDto(
        String name,
        String description,
        BigDecimal price
) {}