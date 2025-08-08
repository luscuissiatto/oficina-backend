package br.com.fiap.oficina.domain.repository;

import br.com.fiap.oficina.domain.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    Optional<Vehicle> findByPlate(String plate);
    List<Vehicle> findByClientId(Long clientId);
    void deleteById(Long id);

}
