package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.application.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Endpoints para análise de dados da oficina")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/average-service-time")
    @Operation(summary = "Calcular tempo médio de execução por serviço")
    public ResponseEntity<List<AverageServiceTimeDto>> getAverageServiceTime() {
        List<AverageServiceTimeDto> result = analyticsService.getAverageServiceTime();
        return ResponseEntity.ok(result);
    }
}