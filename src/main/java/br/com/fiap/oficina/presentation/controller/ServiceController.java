package br.com.fiap.oficina.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.oficina.application.dto.ServiceUpdateRequestDto;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/services")
@Tag(name = "Services", description = "Endpoints para gerenciamento de serviços da oficina")
public class ServiceController {
    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @PostMapping
    @Operation(summary = "Criar um novo serviço")
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = serviceRepository.save(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços disponíveis")
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceRepository.findAll();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um serviço por ID")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um serviço existente")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody ServiceUpdateRequestDto serviceDetailsDto) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(serviceDetailsDto.getName());
                    service.setDescription(serviceDetailsDto.getDescription());
                    service.setPrice(serviceDetailsDto.getPrice());

                    Service updatedService = serviceRepository.save(service);
                    return ResponseEntity.ok(updatedService);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um serviço")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        if (serviceRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        serviceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
