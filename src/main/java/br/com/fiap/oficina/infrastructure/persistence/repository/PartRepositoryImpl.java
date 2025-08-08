package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.repository.PartRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.PartDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.PartData;

@Repository
public class PartRepositoryImpl implements PartRepository {

    private final PartDao partDao;

    public PartRepositoryImpl(PartDao partDao) {
        this.partDao = partDao;
    }

    private Part toDomain(PartData data) {
        Part part = new Part(data.getName(), data.getDescription(), data.getPrice(), data.getStockQuantity());
        part.setId(data.getId());
        return part;
    }

    private PartData toData(Part domain) {
        PartData data = new PartData();
        data.setId(domain.getId());
        data.setName(domain.getName());
        data.setDescription(domain.getDescription());
        data.setPrice(domain.getPrice());
        data.setStockQuantity(domain.getStockQuantity());
        return data;
    }

    @Override
    public Part save(Part part) {
        PartData data = toData(part);
        PartData savedData = partDao.save(data);
        return toDomain(savedData);
    }

    @Override
    public Optional<Part> findById(Long id) {
        return partDao.findById(id).map(this::toDomain);
    }



    @Override
    public List<Part> findAll() {
        return partDao.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        partDao.deleteById(id);
    }
}