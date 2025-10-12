package br.com.fiap.oficina.application.dto;

import java.math.BigDecimal;

public record PartRequestDto(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {}