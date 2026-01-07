package br.com.fiap.oficina.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.fiap.oficina.domain.model.OrderItem;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.PartRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.ServiceOrderDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.ClientData;
import br.com.fiap.oficina.infrastructure.persistence.entity.OrderItemData;
import br.com.fiap.oficina.infrastructure.persistence.entity.PartData;
import br.com.fiap.oficina.infrastructure.persistence.entity.ServiceData;
import br.com.fiap.oficina.infrastructure.persistence.entity.ServiceOrderData;
import br.com.fiap.oficina.infrastructure.persistence.entity.VehicleData;

@Repository
public class ServiceOrderRepositoryImpl implements ServiceOrderRepository {

    private final ServiceOrderDao serviceOrderDao;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final PartRepository partRepository;

    public ServiceOrderRepositoryImpl(ServiceOrderDao serviceOrderDao, ClientRepository clientRepository, VehicleRepository vehicleRepository, ServiceRepository serviceRepository, PartRepository partRepository) {
        this.serviceOrderDao = serviceOrderDao;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.partRepository = partRepository;
    }

    private ServiceOrderData toData(ServiceOrder domain) {
        ServiceOrderData data = new ServiceOrderData();
        data.setId(domain.getId());
        data.setStatus(domain.getStatus());
        data.setCreationDate(domain.getCreationDate());
        data.setFinishDate(domain.getFinishDate());
        data.setUpdatedAt(domain.getUpdatedAt());
        data.setTotalAmount(domain.getTotalAmount());

        ClientData clientData = new ClientData();
        clientData.setId(domain.getClient().getId());
        data.setClient(clientData);

        VehicleData vehicleData = new VehicleData();
        vehicleData.setId(domain.getVehicle().getId());
        data.setVehicle(vehicleData);

        List<ServiceData> serviceDataList = domain.getServices().stream().map(service -> {
            ServiceData sd = new ServiceData();
            sd.setId(service.getId());
            return sd;
        }).collect(Collectors.toList());
        data.setServices(serviceDataList);

        if (domain.getItems() != null) {
            List<OrderItemData> itemDataList = domain.getItems().stream().map(orderItem -> {
                OrderItemData itemData = new OrderItemData();
                itemData.setId(orderItem.getId());
                itemData.setQuantity(orderItem.getQuantity());

                PartData partData = new PartData();
                partData.setId(orderItem.getPart().getId());
                itemData.setPart(partData);

                itemData.setServiceOrder(data);
                return itemData;
            }).collect(Collectors.toList());
            data.setItems(itemDataList);
        }

        return data;
    }

    @Override
    public ServiceOrder save(ServiceOrder serviceOrder) {
        ServiceOrderData data = toData(serviceOrder);

        ServiceOrderData savedData = serviceOrderDao.save(data);

        return mapDataToDomain(savedData);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return serviceOrderDao.findAll().stream()
                .map(this::mapDataToDomain)
                .toList();
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        return serviceOrderDao.findById(id).map(this::mapDataToDomain);
    }

    @Override
    public List<ServiceOrder> findByClientId(Long clientId) {
        return serviceOrderDao.findByClientId(clientId).stream()
                .map(this::mapDataToDomain)
                .toList();
    }

    private ServiceOrder mapDataToDomain(ServiceOrderData data) {
        ServiceOrder domain = new ServiceOrder();
        domain.setId(data.getId());
        domain.setStatus(data.getStatus());
        domain.setCreationDate(data.getCreationDate());
        domain.setFinishDate(data.getFinishDate());
        domain.setUpdatedAt(data.getUpdatedAt());
        domain.setTotalAmount(data.getTotalAmount());

        domain.setClient(clientRepository.findById(data.getClient().getId()).orElse(null));
        domain.setVehicle(vehicleRepository.findById(data.getVehicle().getId()).orElse(null));

        if (data.getServices() != null) {
            domain.setServices(data.getServices().stream()
                    .map(sd -> serviceRepository.findById(sd.getId()).orElse(null))
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        if (data.getItems() != null) {
            List<OrderItem> orderItems = data.getItems().stream().map(itemData -> {
                Part part = partRepository.findById(itemData.getPart().getId()).orElse(null);
                OrderItem orderItem = new OrderItem(part, itemData.getQuantity());
                orderItem.setId(itemData.getId());
                return orderItem;
            }).collect(Collectors.toCollection(ArrayList::new));
            domain.setItems(orderItems);
        }

        return domain;
    }

    @Override
    public Optional<ServiceOrder> findActiveByClientDocumentAndVehiclePlate(String document, String plate) {
        List<OrderStatus> inactiveStatus = List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED);

        return serviceOrderDao
                .findActiveByClientDocumentAndVehiclePlate(document, plate, inactiveStatus)
                .map(this::mapDataToDomain);
    }

    @Override
    public boolean existsByClientId(Long clientId) {
        return serviceOrderDao.existsByClientId(clientId);
    }

    @Override
    public List<ServiceOrder> findAllByStatusIn(List<OrderStatus> statuses) {
        List<ServiceOrderData> dataList = serviceOrderDao.findAllByStatusIn(statuses);

        return dataList.stream()
                .map(this::mapDataToDomain)
                .collect(Collectors.toList());
    }
}