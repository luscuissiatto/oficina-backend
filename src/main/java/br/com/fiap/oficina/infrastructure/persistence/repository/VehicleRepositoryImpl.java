package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.VehicleDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.ClientData;
import br.com.fiap.oficina.infrastructure.persistence.entity.VehicleData;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {
    private VehicleDao vehicleDao;
    private ClientRepository clientRepository;

    public VehicleRepositoryImpl(VehicleDao vehicleDao, ClientRepository clientRepository) {
        this.vehicleDao = vehicleDao;
        this.clientRepository = clientRepository;
    }

    private Vehicle toDomain(VehicleData data) {
        Client clientDomain = clientRepository.findById(data.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Client not found for vehicle"));

        Vehicle vehicle = new Vehicle(data.getPlate(), data.getBrand(), data.getModel(), data.getYear(), clientDomain);
        vehicle.setId(data.getId());
        return vehicle;
    }

    private VehicleData toData(Vehicle domain) {
        VehicleData data = new VehicleData();
        data.setId(domain.getId());
        data.setPlate(domain.getPlate());
        data.setBrand(domain.getBrand());
        data.setModel(domain.getModel());
        data.setYear(domain.getYear());

        ClientData clientData = new ClientData();
        clientData.setId(domain.getClient().getId());
        data.setClient(clientData);

        return data;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleData data = toData(vehicle);
        VehicleData savedData = vehicleDao.save(data);
        return toDomain(savedData);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleDao.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Vehicle> findByPlate(String placa) {
        return vehicleDao.findByPlate(placa).map(this::toDomain);
    }

    @Override
    public List<Vehicle> findByClientId(Long clienteId) {
        return vehicleDao.findByClientId(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        vehicleDao.deleteById(id);
    }
}
