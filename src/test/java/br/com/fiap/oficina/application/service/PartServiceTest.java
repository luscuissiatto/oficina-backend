package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.PartRequestDto;
import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.repository.PartRepository;
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
public class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartService partService;

    @Test
    public void deveCriarPecaComSucesso() {
        // Arrange
        var requestDto = new PartRequestDto("Vela", "NGK", BigDecimal.TEN, 10);
        when(partRepository.save(any(Part.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Part createdPart = partService.create(requestDto);

        // Assert
        assertNotNull(createdPart);
        assertEquals("Vela", createdPart.getName());
        verify(partRepository, times(1)).save(any(Part.class));
    }

    @Test
    public void deveLancarExcecao_aoBuscarPecaInexistente() {
        // Arrange
        long partId = 99L;
        when(partRepository.findById(partId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> partService.findById(partId));
    }

    @Test
    public void deveDeletarPecaComSucesso() {
        long partId = 1L;
        Part part = new Part("Pneu", "Aro 15", BigDecimal.TEN, 1);
        part.setId(partId);

        when(partRepository.findById(partId)).thenReturn(Optional.of(part));
        doNothing().when(partRepository).deleteById(partId);

        assertDoesNotThrow(() -> partService.delete(partId));

        verify(partRepository, times(1)).deleteById(partId);
    }
}