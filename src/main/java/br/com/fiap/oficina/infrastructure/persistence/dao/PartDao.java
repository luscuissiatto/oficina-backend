package br.com.fiap.oficina.infrastructure.persistence.dao;

import br.com.fiap.oficina.infrastructure.persistence.entity.PartData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartDao extends JpaRepository<PartData, Long> {
}