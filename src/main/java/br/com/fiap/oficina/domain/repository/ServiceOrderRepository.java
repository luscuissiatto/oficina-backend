package br.com.fiap.oficina.domain.repository;

import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository {
    ServiceOrder save(ServiceOrder serviceOrder);
    List<ServiceOrder> findAll();
    Optional<ServiceOrder> findById(Long id);
    List<ServiceOrder> findByClientId(Long clientId);
    List<ServiceOrder> findAllByStatusIn(List<OrderStatus> statuses);
    Optional<ServiceOrder> findActiveByClientDocumentAndVehiclePlate(String document, String plate);
    boolean existsByClientId(Long clientId);
}