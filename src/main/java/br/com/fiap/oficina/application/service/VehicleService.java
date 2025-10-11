package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    public VehicleService(VehicleRepository vehicleRepository, ClientRepository clientRepository) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    public Vehicle create(VehicleRequestDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Vehicle newVehicle = new Vehicle(dto.getPlate(), dto.getBrand(), dto.getModel(), dto.getYear(), client);
        return vehicleRepository.save(newVehicle);
    }

    public List<Vehicle> findByClientId(Long clientId) {
        // Valida se o cliente existe antes de buscar os veículos
        if (!clientRepository.existsById(clientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        return vehicleRepository.findByClientId(clientId);
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado"));
    }

    public Vehicle update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = findById(id); // Reutiliza o findById
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        vehicle.setPlate(dto.getPlate());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setClient(client);

        return vehicleRepository.save(vehicle);
    }

    public void delete(Long id) {
        Vehicle vehicle = findById(id); // Garante que o veículo existe
        vehicleRepository.deleteById(vehicle.getId());
    }
}