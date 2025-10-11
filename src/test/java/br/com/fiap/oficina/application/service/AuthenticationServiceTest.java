package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.AuthenticationRequestDto;
import br.com.fiap.oficina.application.dto.AuthenticationResponseDto;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import br.com.fiap.oficina.infrastructure.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Usa a extensão do Mockito para testes de unidade puros
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager; // Mock da dependência

    @Mock
    private TokenService tokenService; // Mock da dependência

    @InjectMocks
    private AuthenticationService authenticationService; // Classe que estamos testando

    @Test
    public void deveRetornarTokenQuandoLoginForBemSucedido() {
        // Arrange (Preparação)
        var loginDto = new AuthenticationRequestDto();
        loginDto.setUsername("user");
        loginDto.setPassword("pass");
        var user = new UserData();
        user.setUsername("user");

        // Simula o objeto de autenticação que o Spring Security retornaria
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        // Define o comportamento dos mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(user)).thenReturn("mock.jwt.token");

        // Act (Ação)
        AuthenticationResponseDto response = authenticationService.login(loginDto);

        // Assert (Verificação)
        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());

        // Verifica se os mocks foram chamados como esperado
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, times(1)).generateToken(user);
    }
}