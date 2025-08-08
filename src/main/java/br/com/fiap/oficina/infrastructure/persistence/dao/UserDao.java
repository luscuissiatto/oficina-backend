package br.com.fiap.oficina.infrastructure.persistence.dao;

import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<UserData, Long> {
    Optional<UserData> findByUsername(String username);
}
