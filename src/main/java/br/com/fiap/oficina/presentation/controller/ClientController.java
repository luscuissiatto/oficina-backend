package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ClientRequestDto;
import br.com.fiap.oficina.application.dto.ClientUpdateRequestDto;
import br.com.fiap.oficina.application.service.ClientService;
import br.com.fiap.oficina.domain.model.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Endpoints para gerenciamento de clientes")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientRequestDto clientDto) {
        Client savedClient = clientService.create(clientDto);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<List<Client>> getAllClients(@RequestParam(name = "document", required = false) String document) {
        List<Client> clients = clientService.findAllByDocument(document);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente por ID")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente existente")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequestDto clientDetailsDto) {
        Client updatedClient = clientService.update(id, clientDetailsDto);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um cliente")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}