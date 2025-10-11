package br.com.fiap.oficina.domain.repository;

import br.com.fiap.oficina.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findByDocument(String document);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
