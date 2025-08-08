package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Endpoints para gerenciamento de veículos")
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    public VehicleController(VehicleRepository vehicleRepository, ClientRepository clientRepository) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    @Operation(summary = "Criar um novo veículo para um cliente")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequestDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Vehicle newVehicle = new Vehicle(dto.getPlate(), dto.getBrand(), dto.getModel(), dto.getYear(), client);

        Vehicle savedVehicle = vehicleRepository.save(newVehicle);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar todos os veículos de um cliente")
    public ResponseEntity<List<Vehicle>> getVehiclesByClientId(@PathVariable Long clientId) {
        List<Vehicle> vehicles = vehicleRepository.findByClientId(clientId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("vehicle/{id}")
    @Operation(summary = "Buscar um veículo pelo id")
    public ResponseEntity<Vehicle> findById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um veículo")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDto vehicleDetailsDto) {
        return vehicleRepository.findById(id).map(vehicle -> {
            Client client = clientRepository.findById(vehicleDetailsDto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            vehicle.setPlate(vehicleDetailsDto.getPlate());
            vehicle.setBrand(vehicleDetailsDto.getBrand());
            vehicle.setModel(vehicleDetailsDto.getModel());
            vehicle.setYear(vehicleDetailsDto.getYear());
            vehicle.setClient(client);

            Vehicle updatedVehicle = vehicleRepository.save(vehicle);
            return ResponseEntity.ok(updatedVehicle);

        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um veículo")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    vehicleRepository.deleteById(id);

                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
