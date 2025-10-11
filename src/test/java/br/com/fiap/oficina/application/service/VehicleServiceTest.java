package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private VehicleService vehicleService;

    @Test
    public void deveCriarVeiculo_quandoClienteExiste() {
        // Arrange
        var client = new Client("Cliente Teste", "123");
        client.setId(1L);
        var requestDto = new VehicleRequestDto();
        requestDto.setBrand("Marca");
        requestDto.setModel("Modelo");
        requestDto.setYear(2023);
        requestDto.setPlate("ABC-1234");
        requestDto.setClientId(client.getId());

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Vehicle createdVehicle = vehicleService.create(requestDto);

        // Assert
        assertNotNull(createdVehicle);
        assertEquals("ABC-1234", createdVehicle.getPlate());
        assertEquals(client, createdVehicle.getClient());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void deveLancarExcecao_aoCriarVeiculo_quandoClienteNaoExiste() {
        // Arrange
        var requestDto = new VehicleRequestDto();
        requestDto.setBrand("Marca");
        requestDto.setModel("Modelo");
        requestDto.setYear(2023);
        requestDto.setPlate("ABC-1234");
        requestDto.setClientId(99L);

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> vehicleService.create(requestDto));
        verify(vehicleRepository, never()).save(any(Vehicle.class)); // Garante que o save nunca foi chamado
    }

    @Test
    public void deveLancarExcecao_aoBuscarVeiculosDeClienteInexistente() {
        // Arrange
        long clientId = 99L;
        when(clientRepository.existsById(clientId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> vehicleService.findByClientId(clientId));
        verify(vehicleRepository, never()).findByClientId(anyLong()); // Garante que a busca nunca foi feita
    }
}