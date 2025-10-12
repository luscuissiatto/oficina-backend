package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.ClientRequestDto;
import br.com.fiap.oficina.application.dto.ClientUpdateRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public ClientService(ClientRepository clientRepository, ServiceOrderRepository serviceOrderRepository) {
        this.clientRepository = clientRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public Client create(ClientRequestDto clientDto) {
        Client newClient = new Client();
        newClient.setName(clientDto.getName());
        newClient.setDocument(clientDto.getDocument());
        return clientRepository.save(newClient);
    }

    public List<Client> findAllByDocument(String document) {
        if (document != null && !document.isBlank()) {
            return clientRepository.findByDocument(document)
                    .map(List::of)
                    .orElse(List.of());
        }
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public Client update(Long id, ClientUpdateRequestDto clientDetailsDto) {
        Client client = findById(id);

        if (clientDetailsDto.getName() != null) {
            client.setName(clientDetailsDto.getName());
        }
        if (clientDetailsDto.getDocument() != null) {
            client.setDocument(clientDetailsDto.getDocument());
        }

        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);

        if (serviceOrderRepository.existsByClientId(client.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir um cliente que possui ordens de serviço.");
        }

        clientRepository.deleteById(client.getId());
    }
}