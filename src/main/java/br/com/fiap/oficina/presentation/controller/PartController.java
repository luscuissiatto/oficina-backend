package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.PartRequestDto;
import br.com.fiap.oficina.application.service.PartService;
import br.com.fiap.oficina.domain.model.Part;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parts")
@Tag(name = "Parts", description = "Endpoints para gerenciamento de peças e insumos")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova peça ou insumo")
    public ResponseEntity<Part> createPart(@RequestBody PartRequestDto dto) {
        Part savedPart = partService.create(dto);
        return new ResponseEntity<>(savedPart, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas as peças e insumos")
    public ResponseEntity<List<Part>> getAllParts() {
        return ResponseEntity.ok(partService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma peça por ID")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma peça existente")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @RequestBody PartRequestDto dto) {
        return ResponseEntity.ok(partService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma peça")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.delete(id);
        return ResponseEntity.noContent().build();
    }
}