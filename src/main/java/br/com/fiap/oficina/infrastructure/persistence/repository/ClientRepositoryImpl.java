package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.ClientDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.ClientData;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientDao clientDao;

    public ClientRepositoryImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    private Client toDomain(ClientData data) {
        Client client = new Client(data.getName(), data.getDocument());
        client.setId(data.getId());
        return client;
    }

    private ClientData toData(Client domain) {
        ClientData data = new ClientData(domain.getName(), domain.getDocument());
        data.setId(domain.getId());
        return data;
    }

    @Override
    public Client save(Client client) {
        ClientData data = toData(client);
        ClientData savedData = clientDao.save(data);
        return toDomain(savedData);
    }

    @Override
    public Optional<Client> findByDocument(String document) {
        return clientDao.findByDocument(document).map(this::toDomain);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientDao.findById(id).map(this::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return clientDao.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        clientDao.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return clientDao.existsById(id);
    }
}
