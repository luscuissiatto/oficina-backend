package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository; // <--- Importe isso

import br.com.fiap.oficina.domain.model.Client; // <--- Importe isso
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.ClientDao;
import br.com.fiap.oficina.infrastructure.persistence.dao.UserDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.ClientData;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientDao clientDao;
    private final UserDao userDao; // 1. Adicione o UserDao aqui

    // 2. Injete o UserDao no construtor
    public ClientRepositoryImpl(ClientDao clientDao, UserDao userDao) {
        this.clientDao = clientDao;
        this.userDao = userDao;
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
        // 1. Salva na tabela CLIENTS (Normal)
        ClientData data = toData(client);
        ClientData savedData = clientDao.save(data);

        // 2. Salva na tabela USERS (O Pulo do Gato 🐱)
        // Verificamos se já existe para não duplicar ou dar erro em update
        if (userDao.findByUsername(client.getDocument()).isEmpty()) {
            UserData newUser = new UserData();
            newUser.setUsername(client.getDocument()); // O CPF vira o username
            newUser.setPassword(""); // Senha vazia (já que login é só CPF)
            userDao.save(newUser);
        }

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
        // Opcional: Se deletar cliente, deletar usuário também?
        // Por enquanto, vamos manter simples e deletar só o cliente
        clientDao.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return clientDao.existsById(id);
    }
}