package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.UpdateStatusRequestDto;
import br.com.fiap.oficina.domain.model.*;
import br.com.fiap.oficina.domain.repository.*;
import br.com.fiap.oficina.infrastructure.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock private ServiceOrderRepository serviceOrderRepository;
    @Mock private PartRepository partRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    @Test
    void testUpdateStatus_ShouldDebitStock_WhenMovingToInProgress() {
        // 1. Arrange (Preparação)
        long orderId = 1L;

        // Cria uma peça com estoque
        Part part = new Part();
        part.setId(10L);
        part.setName("Filtro de Óleo");
        part.setStockQuantity(10);

        // Cria um item na ordem de serviço
        OrderItem orderItem = new OrderItem(part, 2); // Precisa de 2 unidades

        // Cria a Ordem de Serviço
        ServiceOrder order = new ServiceOrder();
        order.setId(orderId);
        order.setStatus(OrderStatus.AWAITING_APPROVAL); // Status antigo
        order.setItems(List.of(orderItem));

        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto();
        requestDto.setNewStatus("IN_PROGRESS");

        // Configura o comportamento dos mocks
        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act (Ação)
        ServiceOrder updatedOrder = serviceOrderService.updateStatus(orderId, requestDto);

        // 3. Assert (Verificação)
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.IN_PROGRESS, updatedOrder.getStatus());

        // Verifica se o estoque da peça foi debitado corretamente
        assertEquals(8, part.getStockQuantity());

        // Verifica se o método save do PartRepository foi chamado para persistir a alteração do estoque
        verify(partRepository, times(1)).save(part);
        verify(serviceOrderRepository, times(1)).save(order);
    }

    @Test
    public void deveLancarExcecao_quandoEstoqueForInsuficiente() {
        // Arrange
        Part partSemEstoque = new Part();
        partSemEstoque.setStockQuantity(1); // Só 1 em estoque
        OrderItem item = new OrderItem(partSemEstoque, 2); // Precisa de 2
        ServiceOrder order = new ServiceOrder();
        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        order.setItems(List.of(item));
        var requestDto = new UpdateStatusRequestDto();
        requestDto.setNewStatus("IN_PROGRESS");

        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        // Verifica se a exceção correta é lançada
        assertThrows(ResponseStatusException.class, () -> {
            serviceOrderService.updateStatus(1L, requestDto);
        });

        // Garante que nenhuma alteração foi salva no banco
        verify(partRepository, never()).save(any());
        verify(serviceOrderRepository, never()).save(any());
    }

    @Test
    public void deveEnviarEmail_aoMudarStatusParaAguardandoAprovacao() {
        // Arrange
        ServiceOrder order = new ServiceOrder();
        order.setStatus(OrderStatus.IN_DIAGNOSIS); // Status antigo
        var requestDto = new UpdateStatusRequestDto(); // Novo status
        requestDto.setNewStatus("AWAITING_APPROVAL");

        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenReturn(order);

        // Act
        serviceOrderService.updateStatus(1L, requestDto);

        // Assert
        // Verifica se o método de envio de e-mail foi chamado exatamente 1 vez
        verify(emailService, times(1)).sendServiceOrderBudget(order);
    }
}