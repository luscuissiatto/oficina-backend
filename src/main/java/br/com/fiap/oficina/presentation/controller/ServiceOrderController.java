package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.*;
import br.com.fiap.oficina.application.service.ServiceOrderService;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-orders")
@Tag(name = "Service Orders", description = "Endpoints para gerenciamento de Ordens de Serviço")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova Ordem de Serviço")
    public ResponseEntity<ServiceOrder> createServiceOrder(@RequestBody ServiceOrderRequestDto dto) {
        ServiceOrder savedOrder = serviceOrderService.create(dto);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma Ordem de Serviço por ID")
    public ResponseEntity<ServiceOrder> getServiceOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de uma Ordem de Serviço")
    public ResponseEntity<ServiceOrder> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequestDto statusDto) {
        return ResponseEntity.ok(serviceOrderService.updateStatus(id, statusDto));
    }

    @PatchMapping("/{id}/add-items")
    @Operation(summary = "Adicionar novos serviços e peças a uma Ordem de Serviço existente")
    public ResponseEntity<ServiceOrder> addItemsToOrder(@PathVariable Long id, @RequestBody AddItemsToOrderRequestDto dto) {
        return ResponseEntity.ok(serviceOrderService.addItems(id, dto));
    }

    @GetMapping("/status")
    @Operation(summary = "Consultar o status da OS mais recente por documento e placa")
    public ResponseEntity<OrderStatusResponseDto> findOrderStatusByDocumentAndPlate(@RequestParam String document, @RequestParam String plate) {
        return ResponseEntity.ok(serviceOrderService.findStatusByDocumentAndPlate(document, plate));
    }

    @GetMapping
    @Operation(summary = "Listar todas as Ordens de Serviço (visão administrativa)")
    public ResponseEntity<List<ServiceOrderSummaryDto>> getAllServiceOrders() {
        return ResponseEntity.ok(serviceOrderService.findAllSummaries());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar todas as Ordens de Serviço de um cliente específico")
    public ResponseEntity<List<ServiceOrderSummaryDto>> getOrdersByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(serviceOrderService.findSummariesByClientId(clientId));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Aprovar orçamento de uma Ordem de Serviço", 
               description = "Aprova o orçamento de uma ordem de serviço que está aguardando aprovação. " +
                           "Transição: AWAITING_APPROVAL -> IN_PROGRESS. " +
                           "Ao aprovar, o estoque das peças é debitado automaticamente.")
    public ResponseEntity<ServiceOrder> approveBudget(@PathVariable Long id) {
        ServiceOrder approvedOrder = serviceOrderService.approveBudget(id);
        return ResponseEntity.ok(approvedOrder);
    }

    @PostMapping("/{id}/refuse")
    @Operation(summary = "Recusar orçamento de uma Ordem de Serviço",
               description = "Recusa o orçamento de uma ordem de serviço que está aguardando aprovação. " +
                           "Transição: AWAITING_APPROVAL -> REFUSED. " +
                           "A ordem de serviço é finalizada ao recusar.")
    public ResponseEntity<ServiceOrder> refuseBudget(@PathVariable Long id) {
        ServiceOrder refusedOrder = serviceOrderService.refuseBudget(id);
        return ResponseEntity.ok(refusedOrder);
    }
}