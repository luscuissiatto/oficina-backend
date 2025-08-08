package br.com.fiap.oficina.infrastructure.persistence.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.oficina.infrastructure.persistence.entity.ClientData;

public interface ClientDao extends JpaRepository<ClientData, Long> {

    Optional<ClientData> findByDocument(String document);
}
