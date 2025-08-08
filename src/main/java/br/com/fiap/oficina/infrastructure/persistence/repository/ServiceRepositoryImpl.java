package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.ServiceDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.ServiceData;

@Repository
public class ServiceRepositoryImpl implements ServiceRepository {
    private final ServiceDao serviceDao;

    public ServiceRepositoryImpl(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    private Service toDomain(ServiceData data) {
        Service service = new Service(data.getName(), data.getDescription(), data.getPrice());
        service.setId(data.getId());
        return service;
    }

    private ServiceData toData(Service domain) {
        ServiceData data = new ServiceData();
        data.setId(domain.getId());
        data.setName(domain.getName());
        data.setDescription(domain.getDescription());
        data.setPrice(domain.getPrice());
        return data;
    }

    @Override
    public Service save(Service service) {
        ServiceData data = toData(service);
        ServiceData savedData = serviceDao.save(data);
        return toDomain(savedData);
    }

    @Override
    public Optional<Service> findById(Long id) {
        return serviceDao.findById(id).map(this::toDomain);
    }

    @Override
    public List<Service> findAll() {
        return serviceDao.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        serviceDao.deleteById(id);
    }
}
