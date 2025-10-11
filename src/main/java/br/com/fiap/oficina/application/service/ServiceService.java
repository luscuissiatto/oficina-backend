package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.ServiceRequestDto;
import br.com.fiap.oficina.application.dto.ServiceUpdateRequestDto;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Service create(ServiceRequestDto dto) {
        Service newService = new Service();
        newService.setName(dto.name());
        newService.setDescription(dto.description());
        newService.setPrice(dto.price());
        return serviceRepository.save(newService);
    }

    public List<Service> findAll() {
        return serviceRepository.findAll();
    }

    public Service findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
    }

    public Service update(Long id, ServiceUpdateRequestDto dto) {
        Service service = findById(id); // Reutiliza findById para tratar erro 404
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        return serviceRepository.save(service);
    }

    public void delete(Long id) {
        Service service = findById(id); // Garante que o serviço existe
        serviceRepository.deleteById(service.getId());
    }
}