package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.application.service.VehicleService;
import br.com.fiap.oficina.domain.model.Vehicle;
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

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo veículo para um cliente")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequestDto dto) {
        Vehicle savedVehicle = vehicleService.create(dto);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar todos os veículos de um cliente")
    public ResponseEntity<List<Vehicle>> getVehiclesByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(vehicleService.findByClientId(clientId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um veículo pelo id")
    public ResponseEntity<Vehicle> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um veículo")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDto vehicleDetailsDto) {
        return ResponseEntity.ok(vehicleService.update(id, vehicleDetailsDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um veículo")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}