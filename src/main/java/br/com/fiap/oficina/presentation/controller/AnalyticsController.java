package br.com.fiap.oficina.presentation.controller;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Endpoints para análise e monitoramento")
public class AnalyticsController {

    private final ServiceOrderRepository serviceOrderRepository;

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    public AnalyticsController(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;

        logger.info(">>> AnalyticsController bean has been created successfully! <<<");
    }

    @GetMapping("/average-service-time")
    @Operation(summary = "Monitorar o tempo médio de execução dos serviços")
    public ResponseEntity<List<AverageServiceTimeDto>> getAverageServiceExecutionTime() {
        List<ServiceOrder> allOrders = serviceOrderRepository.findAll();

        List<AverageServiceTimeDto> analytics = allOrders.stream()
                .filter(order -> (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.DELIVERED)
                        && order.getCreationDate() != null && order.getFinishDate() != null)
                .flatMap(order -> {
                    Duration duration = Duration.between(order.getCreationDate(), order.getFinishDate());
                    long durationInMinutes = duration.toMinutes();
                    return order.getServices().stream().map(service -> new TempServiceDuration(service.getName(), durationInMinutes));
                })
                .collect(Collectors.groupingBy(
                        TempServiceDuration::serviceName,
                        Collectors.averagingDouble(TempServiceDuration::durationInMinutes)
                ))
                .entrySet().stream()
                .map(entry -> new AverageServiceTimeDto(entry.getKey(), entry.getValue() / 60.0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(analytics);
    }

    private record TempServiceDuration(String serviceName, long durationInMinutes) {}
}