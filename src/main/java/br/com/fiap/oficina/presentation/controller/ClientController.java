package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Endpoints para gerenciamento de clientes")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        Client savedClient = clientRepository.save(client);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes", description = "Lista todos os clientes ou busca um cliente específico por documento (CPF/CNPJ).")
    public ResponseEntity<List<Client>> getAllClients(@RequestParam(name = "document", required = false) String document) {
        if (document != null && !document.isBlank()) {
            List<Client> foundClients = clientRepository.findByDocument(document)
                    .map(List::of)
                    .orElse(Collections.emptyList());
            return ResponseEntity.ok(foundClients);
        } else {
            List<Client> allClients = clientRepository.findAll();
            return ResponseEntity.ok(allClients);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente por ID")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente existente")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @Valid @RequestBody Client clientDetails) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(clientDetails.getName());
                    client.setDocument(clientDetails.getDocument());
                    Client updatedClient = clientRepository.save(client);
                    return ResponseEntity.ok(updatedClient);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um cliente")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);

        if (clientOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
