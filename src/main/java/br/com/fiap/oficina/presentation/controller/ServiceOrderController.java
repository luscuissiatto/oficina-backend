package br.com.fiap.oficina.presentation.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.oficina.application.dto.AddItemsToOrderRequestDto;
import br.com.fiap.oficina.application.dto.OrderItemRequestDto;
import br.com.fiap.oficina.application.dto.OrderStatusResponseDto;
import br.com.fiap.oficina.application.dto.ServiceOrderRequestDto;
import br.com.fiap.oficina.application.dto.ServiceOrderSummaryDto;
import br.com.fiap.oficina.application.dto.UpdateStatusRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.OrderItem;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.PartRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/service-orders")
@Tag(name = "Service Orders", description = "Endpoints para gerenciamento de Ordens de Serviço")
public class ServiceOrderController {

    private final ServiceOrderRepository serviceOrderRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final PartRepository partRepository;
    private final EmailService emailService;

    public ServiceOrderController(ServiceOrderRepository serviceOrderRepository, ClientRepository clientRepository, VehicleRepository vehicleRepository, ServiceRepository serviceRepository, PartRepository partRepository, EmailService emailService) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.partRepository = partRepository;
        this.emailService = emailService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova Ordem de Serviço")
    public ResponseEntity<ServiceOrder> createServiceOrder(@RequestBody ServiceOrderRequestDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        List<Service> services = dto.getServiceIds().stream()
                .map(id -> serviceRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found: " + id)))
                .collect(Collectors.toList());

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Part part = partRepository.findById(itemDto.getPartId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found: " + itemDto.getPartId()));
            orderItems.add(new OrderItem(part, itemDto.getQuantity()));
        }

        ServiceOrder newOrder = new ServiceOrder();
        newOrder.setClient(client);
        newOrder.setVehicle(vehicle);
        newOrder.setServices(services);
        newOrder.setItems(orderItems);
        newOrder.setStatus(OrderStatus.RECEIVED);
        newOrder.setCreationDate(LocalDateTime.now());

        newOrder.calculateTotalAmount();
        ServiceOrder savedOrder = serviceOrderRepository.save(newOrder);

        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma Ordem de Serviço por ID")
    public ResponseEntity<ServiceOrder> getServiceOrderById(@PathVariable Long id) {
        return serviceOrderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de uma Ordem de Serviço")
    public ResponseEntity<ServiceOrder> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDto statusDto) {

        ServiceOrder serviceOrder = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Order not found"));

        OrderStatus oldStatus = serviceOrder.getStatus();
        OrderStatus newStatus = OrderStatus.valueOf(statusDto.getNewStatus().toUpperCase());

        if (newStatus == OrderStatus.IN_PROGRESS && oldStatus == OrderStatus.AWAITING_APPROVAL) {
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

        serviceOrder.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.DELIVERED) {
            serviceOrder.setFinishDate(LocalDateTime.now());
        }

        ServiceOrder updatedOrder = serviceOrderRepository.save(serviceOrder);

        if (newStatus == OrderStatus.AWAITING_APPROVAL && oldStatus == OrderStatus.IN_DIAGNOSIS) {
            emailService.sendServiceOrderBudget(updatedOrder);
        }

        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/add-items")
    @Operation(summary = "Adicionar novos serviços e peças a uma Ordem de Serviço existente")
    public ResponseEntity<ServiceOrder> addItemsToOrder(
            @PathVariable Long id,
            @RequestBody AddItemsToOrderRequestDto dto) {

        ServiceOrder serviceOrder = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Order not found"));

        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<Service> newServices = dto.getServiceIds().stream()
                    .map(serviceId -> serviceRepository.findById(serviceId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found: " + serviceId)))
                    .toList();

            List<Service> combinedServices = new ArrayList<>(serviceOrder.getServices());
            combinedServices.addAll(newServices);
            serviceOrder.setServices(combinedServices);
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItem> newItems = dto.getItems().stream()
                    .map(itemDto -> {
                        Part part = partRepository.findById(itemDto.getPartId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found: " + itemDto.getPartId()));
                        return new OrderItem(part, itemDto.getQuantity());
                    })
                    .toList();

            List<OrderItem> combinedItems = new ArrayList<>(serviceOrder.getItems());
            combinedItems.addAll(newItems);
            serviceOrder.setItems(combinedItems);
        }

        serviceOrder.calculateTotalAmount();
        ServiceOrder updatedOrder = serviceOrderRepository.save(serviceOrder);

        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/status")
    @Operation(summary = "Consultar o status da OS mais recente por documento e placa (para clientes)")
    public ResponseEntity<OrderStatusResponseDto> findOrderStatusByDocumentAndPlate(
            @RequestParam String document,
            @RequestParam String plate) {

        return serviceOrderRepository.findActiveByClientDocumentAndVehiclePlate(document, plate)
                .map(order -> {
                    OrderStatusResponseDto responseDto = new OrderStatusResponseDto(
                            order.getId(),
                            order.getStatus().toString()
                    );
                    return ResponseEntity.ok(responseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar todas as Ordens de Serviço (visão administrativa)")
    public ResponseEntity<List<ServiceOrderSummaryDto>> getAllServiceOrders() {
        List<ServiceOrder> allOrders = serviceOrderRepository.findAll();

        List<ServiceOrderSummaryDto> summaryList = allOrders.stream()
                .map(order -> new ServiceOrderSummaryDto(
                        order.getId(),
                        order.getClient().getName(),
                        order.getVehicle().getPlate(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getCreationDate()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaryList);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar todas as Ordens de Serviço de um cliente específico")
    public ResponseEntity<List<ServiceOrderSummaryDto>> getOrdersByClientId(@PathVariable Long clientId) {
        List<ServiceOrder> orders = serviceOrderRepository.findByClientId(clientId);

        List<ServiceOrderSummaryDto> summaryList = orders.stream()
                .map(order -> new ServiceOrderSummaryDto(
                        order.getId(),
                        order.getClient().getName(),
                        order.getVehicle().getPlate(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getCreationDate()
                ))
                .toList();

        return ResponseEntity.ok(summaryList);
    }
}