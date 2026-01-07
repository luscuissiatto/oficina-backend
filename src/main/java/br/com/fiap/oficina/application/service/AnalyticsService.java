package br.com.fiap.oficina.application.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@org.springframework.stereotype.Service
public class AnalyticsService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final MeterRegistry meterRegistry;

    public AnalyticsService(ServiceOrderRepository serviceOrderRepository, MeterRegistry meterRegistry) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.meterRegistry = meterRegistry;
    }

    public void recordStatusDuration(String oldStatus, String newStatus, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return;

        long durationSeconds = Duration.between(startDate, endDate).getSeconds();

        Timer.builder("service_order_status_duration")
                .tag("from_status", oldStatus)
                .tag("to_status", newStatus)
                .description("Tempo gasto em cada etapa da OS")
                .register(meterRegistry)
                .record(durationSeconds, TimeUnit.SECONDS);
    }

    public List<AverageServiceTimeDto> getAverageServiceTime() {
        var completedOrders = serviceOrderRepository.findAllByStatusIn(
                List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED)
        );

        var serviceDurations = completedOrders.stream()
                .flatMap(order -> order.getServices().stream().map(service -> {
                    long duration = Duration.between(order.getCreationDate(), order.getFinishDate()).toMinutes();
                    return new SimpleEntry<>(service, duration);
                }))
                .collect(Collectors.toList());

        Map<String, Double> averageTimeInMinutes = serviceDurations.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getName(),
                        Collectors.averagingLong(SimpleEntry::getValue)
                ));

        return averageTimeInMinutes.entrySet().stream()
                .map(entry -> new AverageServiceTimeDto(
                        entry.getKey(),
                        entry.getValue() / 60.0
                ))
                .sorted((a, b) -> a.getServiceName().compareTo(b.getServiceName()))
                .collect(Collectors.toList());
    }

    private static class SimpleEntry<K, V> {
        private final K key;
        private final V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }
}