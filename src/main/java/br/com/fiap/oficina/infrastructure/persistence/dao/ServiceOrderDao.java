package br.com.fiap.oficina.infrastructure.persistence.dao;

import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.infrastructure.persistence.entity.ServiceOrderData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServiceOrderDao extends JpaRepository<ServiceOrderData, Long> {
    List<ServiceOrderData> findByClientId(Long clientId);
    @Query("SELECT so FROM ServiceOrderData so " +
            "WHERE so.client.document = :document " +
            "AND so.vehicle.plate = :plate " +
            "AND so.status NOT IN :statuses " +
            "ORDER BY so.creationDate DESC " +
            "LIMIT 1")
    Optional<ServiceOrderData> findActiveByClientDocumentAndVehiclePlate(
            @Param("document") String document,
            @Param("plate") String plate,
            @Param("statuses") Collection<OrderStatus> statuses);
}