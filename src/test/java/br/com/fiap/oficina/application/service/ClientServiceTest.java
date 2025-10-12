package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ServiceOrderRepository serviceOrderRepository;
    @InjectMocks
    private ClientService clientService;

    @Test
    public void deveDeletarCliente_quandoNaoPossuiOrdemDeServico() {
        long clientId = 1L;
        Client client = new Client("Teste", "12345678901");
        client.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.existsByClientId(clientId)).thenReturn(false);

        assertDoesNotThrow(() -> clientService.delete(clientId));

        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    public void deveLancarExcecao_aoTentarDeletarCliente_comOrdemDeServico() {
        long clientId = 1L;
        Client client = new Client("Teste", "12345678901");
        client.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(serviceOrderRepository.existsByClientId(clientId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> clientService.delete(clientId));

        verify(clientRepository, never()).deleteById(clientId);
    }

    @Test
    public void deveLancarExcecao_aoBuscarClienteInexistente() {
        long clientId = 99L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> clientService.findById(clientId));
    }
}