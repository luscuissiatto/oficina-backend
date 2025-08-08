package br.com.fiap.oficina.infrastructure.persistence.dao;

import br.com.fiap.oficina.infrastructure.persistence.entity.VehicleData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleDao extends JpaRepository<VehicleData, Long> {
    Optional<VehicleData> findByPlate(String plate);
    List<VehicleData> findByClientId(Long clientId);
}
