package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ClientRequestDto;
import br.com.fiap.oficina.application.dto.ClientUpdateRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Endpoints para gerenciamento de clientes")
public class ClientController {
    private final ClientRepository clientRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public ClientController(ClientRepository clientRepository, ServiceOrderRepository serviceOrderRepository) {
        this.clientRepository = clientRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    @PostMapping
    @Operation(summary = "Criar um novo cliente")
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientRequestDto clientDto) {
        Client newClient = new Client();
        newClient.setName(clientDto.getName());
        newClient.setDocument(clientDto.getDocument());

        Client savedClient = clientRepository.save(newClient);
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
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequestDto clientDetailsDto) {
        return clientRepository.findById(id)
                .map(client -> {
                    if (clientDetailsDto.getName() != null) {
                        client.setName(clientDetailsDto.getName());
                    }

                    if (clientDetailsDto.getDocument() != null) {
                        client.setDocument(clientDetailsDto.getDocument());
                    }

                    Client updatedClient = clientRepository.save(client);
                    return ResponseEntity.ok(updatedClient);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um cliente")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (serviceOrderRepository.existsByClientId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir um cliente que possui ordens de serviço.");
        }

        // 3. Se todas as verificações passarem, deleta o cliente
        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
