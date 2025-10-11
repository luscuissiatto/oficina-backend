package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ServiceRequestDto;
import br.com.fiap.oficina.application.dto.ServiceUpdateRequestDto;
import br.com.fiap.oficina.application.service.ServiceService;
import br.com.fiap.oficina.domain.model.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@Tag(name = "Services", description = "Endpoints para gerenciamento de serviços da oficina")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo serviço")
    public ResponseEntity<Service> createService(@RequestBody ServiceRequestDto dto) {
        Service savedService = serviceService.create(dto);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços disponíveis")
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um serviço por ID")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um serviço existente")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody ServiceUpdateRequestDto serviceDetailsDto) {
        return ResponseEntity.ok(serviceService.update(id, serviceDetailsDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um serviço")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}