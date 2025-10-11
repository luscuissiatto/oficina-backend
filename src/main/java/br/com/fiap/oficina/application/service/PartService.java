package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.PartRequestDto;
import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.repository.PartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part create(PartRequestDto dto) {
        Part newPart = new Part();
        newPart.setName(dto.name());
        newPart.setDescription(dto.description());
        newPart.setPrice(dto.price());
        newPart.setStockQuantity(dto.stockQuantity());
        return partRepository.save(newPart);
    }

    public List<Part> findAll() {
        return partRepository.findAll();
    }

    public Part findById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada"));
    }

    public Part update(Long id, PartRequestDto dto) {
        Part part = findById(id); // Reutiliza o findById que já trata o erro 404
        part.setName(dto.name());
        part.setDescription(dto.description());
        part.setPrice(dto.price());
        part.setStockQuantity(dto.stockQuantity());
        return partRepository.save(part);
    }

    public void delete(Long id) {
        Part part = findById(id); // Garante que a peça existe antes de tentar deletar
        partRepository.deleteById(part.getId());
    }
}