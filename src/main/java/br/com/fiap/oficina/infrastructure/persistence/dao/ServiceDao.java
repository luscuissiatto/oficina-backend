package br.com.fiap.oficina.infrastructure.persistence.dao;

import br.com.fiap.oficina.infrastructure.persistence.entity.ServiceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDao extends JpaRepository<ServiceData, Long> {
}
