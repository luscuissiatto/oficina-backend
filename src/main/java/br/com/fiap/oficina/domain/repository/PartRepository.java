package br.com.fiap.oficina.domain.repository;

import br.com.fiap.oficina.domain.model.Part;
import java.util.List;
import java.util.Optional;

public interface PartRepository {
    Part save(Part part);
    Optional<Part> findById(Long id);
    List<Part> findAll();
    void deleteById(Long id);
}