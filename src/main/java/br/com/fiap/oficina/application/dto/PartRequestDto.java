package br.com.fiap.oficina.application.dto;

import java.math.BigDecimal;

// Usar um 'record' é uma forma moderna e concisa de criar DTOs imutáveis.
public record PartRequestDto(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {}