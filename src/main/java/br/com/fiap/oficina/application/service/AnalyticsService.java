package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AnalyticsService {

    private final ServiceOrderRepository serviceOrderRepository;

    public AnalyticsService(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
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