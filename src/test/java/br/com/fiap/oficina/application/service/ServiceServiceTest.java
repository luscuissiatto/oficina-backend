package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.ServiceRequestDto;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    @Test
    public void deveCriarServicoComSucesso() {
        // Arrange
        var requestDto = new ServiceRequestDto("Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Service createdService = serviceService.create(requestDto);

        // Assert
        assertNotNull(createdService);
        assertEquals("Troca de Óleo", createdService.getName());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    public void deveLancarExcecao_aoBuscarServicoInexistente() {
        // Arrange
        long serviceId = 99L;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> serviceService.findById(serviceId));
    }

    @Test
    public void deveDeletarServicoComSucesso() {
        // Arrange
        long serviceId = 1L;
        var service = new Service("Alinhamento", "Alinhar rodas", BigDecimal.TEN);
        service.setId(serviceId);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        doNothing().when(serviceRepository).deleteById(serviceId);

        // Act
        assertDoesNotThrow(() -> serviceService.delete(serviceId));

        // Assert
        verify(serviceRepository, times(1)).deleteById(serviceId);
    }
}