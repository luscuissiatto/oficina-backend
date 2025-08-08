package br.com.fiap.oficina.domain.repository;

import br.com.fiap.oficina.domain.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    Service save(Service service);
    Optional<Service> findById(Long id);
    List<Service> findAll();
    void deleteById(Long id);
}
