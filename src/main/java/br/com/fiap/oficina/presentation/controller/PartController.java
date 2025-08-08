package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.repository.PartRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parts")
@Tag(name = "Parts", description = "Endpoints para gerenciamento de peças e insumos")
public class PartController {

    private final PartRepository partRepository;

    public PartController(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova peça ou insumo")
    public ResponseEntity<Part> createPart(@RequestBody Part part) {
        Part savedPart = partRepository.save(part);
        return new ResponseEntity<>(savedPart, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas as peças e insumos")
    public ResponseEntity<List<Part>> getAllParts() {
        List<Part> parts = partRepository.findAll();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma peça por ID")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return partRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma peça existente")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        return partRepository.findById(id)
                .map(part -> {
                    part.setName(partDetails.getName());
                    part.setDescription(partDetails.getDescription());
                    part.setPrice(partDetails.getPrice());
                    part.setStockQuantity(partDetails.getStockQuantity());
                    Part updatedPart = partRepository.save(part);
                    return ResponseEntity.ok(updatedPart);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma peça")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        Optional<Part> partOptional = partRepository.findById(id);

        if (partOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        partRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}