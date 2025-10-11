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
        // 1. Busca todas as ordens de serviço com status COMPLETED ou DELIVERED
        var completedOrders = serviceOrderRepository.findAllByStatusIn(
                List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED)
        );

        // 2. Mapeia os dados para uma estrutura mais fácil de processar (Serviço -> Duração)
        var serviceDurations = completedOrders.stream()
                // Para cada ordem, pega a lista de serviços dela
                .flatMap(order -> order.getServices().stream().map(service -> {
                    // Calcula a duração em minutos entre a criação e a finalização da OS
                    long duration = Duration.between(order.getCreationDate(), order.getFinishDate()).toMinutes();
                    // Retorna um par simples contendo o serviço e a duração
                    return new SimpleEntry<>(service, duration);
                }))
                .collect(Collectors.toList());

        // 3. Agrupa por nome do serviço e calcula a média das durações
        Map<String, Double> averageTimeInMinutes = serviceDurations.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getName(), // Agrupa pelo nome do serviço
                        Collectors.averagingLong(SimpleEntry::getValue) // Calcula a média das durações (em minutos)
                ));

        // 4. Converte o mapa de médias para a lista de DTOs, transformando minutos em horas
        return averageTimeInMinutes.entrySet().stream()
                .map(entry -> new AverageServiceTimeDto(
                        entry.getKey(),
                        entry.getValue() / 60.0 // Converte minutos para horas
                ))
                // Ordena pelo nome do serviço para ter uma resposta consistente
                .sorted((a, b) -> a.getServiceName().compareTo(b.getServiceName()))
                .collect(Collectors.toList());
    }

    // Classe auxiliar para facilitar o mapeamento
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