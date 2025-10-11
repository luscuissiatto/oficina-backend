package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.*;
import br.com.fiap.oficina.domain.model.*;
import br.com.fiap.oficina.domain.repository.*;
import br.com.fiap.oficina.infrastructure.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final PartRepository partRepository;
    private final EmailService emailService;

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository, ClientRepository clientRepository, VehicleRepository vehicleRepository, ServiceRepository serviceRepository, PartRepository partRepository, EmailService emailService) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.partRepository = partRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ServiceOrder create(ServiceOrderRequestDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado"));

        List<br.com.fiap.oficina.domain.model.Service> services = dto.getServiceIds().stream()
                .map(id -> serviceRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado: " + id)))
                .collect(Collectors.toList());

        List<OrderItem> orderItems = dto.getItems().stream()
                .map(itemDto -> {
                    Part part = partRepository.findById(itemDto.getPartId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada: " + itemDto.getPartId()));
                    return new OrderItem(part, itemDto.getQuantity());
                }).collect(Collectors.toList());

        ServiceOrder newOrder = new ServiceOrder();
        newOrder.setClient(client);
        newOrder.setVehicle(vehicle);
        newOrder.setServices(services);
        newOrder.setItems(orderItems);
        newOrder.setStatus(OrderStatus.RECEIVED);
        newOrder.setCreationDate(LocalDateTime.now());
        newOrder.calculateTotalAmount();

        return serviceOrderRepository.save(newOrder);
    }

    public ServiceOrder findById(Long id) {
        return serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de Serviço não encontrada"));
    }

    @Transactional
    public ServiceOrder updateStatus(Long id, @Valid UpdateStatusRequestDto statusDto) {
        ServiceOrder serviceOrder = findById(id);
        OrderStatus oldStatus = serviceOrder.getStatus();
        OrderStatus newStatus;

        try {
            newStatus = OrderStatus.valueOf(statusDto.getNewStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de status inválido");
        }

        if (newStatus == OrderStatus.IN_PROGRESS && oldStatus == OrderStatus.AWAITING_APPROVAL) {
            debitPartsFromStock(serviceOrder);
        }

        serviceOrder.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.DELIVERED) {
            serviceOrder.setFinishDate(LocalDateTime.now());
        }

        ServiceOrder updatedOrder = serviceOrderRepository.save(serviceOrder);

        if (newStatus == OrderStatus.AWAITING_APPROVAL && oldStatus == OrderStatus.IN_DIAGNOSIS) {
            emailService.sendServiceOrderBudget(updatedOrder);
        }

        return updatedOrder;
    }

    private void debitPartsFromStock(ServiceOrder serviceOrder) {
        for (OrderItem item : serviceOrder.getItems()) {
            Part part = item.getPart();
            int quantityNeeded = item.getQuantity();

            if (part.getStockQuantity() < quantityNeeded) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Estoque insuficiente para a peça '" + part.getName() + "'. Necessário: "
                                + quantityNeeded + ", Disponível: " + part.getStockQuantity());
            }
        }

        for (OrderItem item : serviceOrder.getItems()) {
            Part part = item.getPart();
            int quantityNeeded = item.getQuantity();
            part.setStockQuantity(part.getStockQuantity() - quantityNeeded);
            partRepository.save(part);
        }
    }

    @Transactional
    public ServiceOrder addItems(Long id, AddItemsToOrderRequestDto dto) {
        ServiceOrder serviceOrder = findById(id);

        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            dto.getServiceIds().stream()
                    .map(serviceId -> serviceRepository.findById(serviceId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado: " + serviceId)))
                    .forEach(serviceOrder.getServices()::add);
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            dto.getItems().stream()
                    .map(itemDto -> {
                        Part part = partRepository.findById(itemDto.getPartId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada: " + itemDto.getPartId()));
                        return new OrderItem(part, itemDto.getQuantity());
                    })
                    .forEach(serviceOrder.getItems()::add);
        }

        serviceOrder.calculateTotalAmount();
        return serviceOrderRepository.save(serviceOrder);
    }

    public OrderStatusResponseDto findStatusByDocumentAndPlate(String document, String plate) {
        return serviceOrderRepository.findActiveByClientDocumentAndVehiclePlate(document, plate)
                .map(order -> new OrderStatusResponseDto(order.getId(), order.getStatus().toString()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de serviço ativa encontrada para os dados informados"));
    }

    public List<ServiceOrderSummaryDto> findAllSummaries() {
        Map<OrderStatus, Integer> statusOrder = new EnumMap<>(OrderStatus.class);
        statusOrder.put(OrderStatus.IN_PROGRESS, 1);
        statusOrder.put(OrderStatus.AWAITING_APPROVAL, 2);
        statusOrder.put(OrderStatus.IN_DIAGNOSIS, 3);
        statusOrder.put(OrderStatus.RECEIVED, 4);

        List<ServiceOrder> activeOrders = serviceOrderRepository.findAllByStatusIn(
                List.of(
                        OrderStatus.IN_PROGRESS,
                        OrderStatus.AWAITING_APPROVAL,
                        OrderStatus.IN_DIAGNOSIS,
                        OrderStatus.RECEIVED
                )
        );

        return activeOrders.stream()
                .map(this::mapToSummaryDto)
                .sorted(Comparator
                        .comparing((ServiceOrderSummaryDto dto) -> statusOrder.get(dto.getStatus()))
                        .thenComparing(ServiceOrderSummaryDto::getCreationDate)
                )
                .collect(Collectors.toList());
    }

    public List<ServiceOrderSummaryDto> findSummariesByClientId(Long clientId) {
        return serviceOrderRepository.findByClientId(clientId).stream()
                .map(this::mapToSummaryDto)
                .toList();
    }

    private ServiceOrderSummaryDto mapToSummaryDto(ServiceOrder order) {
        return new ServiceOrderSummaryDto(
                order.getId(),
                order.getClient().getName(),
                order.getVehicle().getPlate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreationDate()
        );
    }
}