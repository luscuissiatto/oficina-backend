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

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void deveRetornarTokenQuandoLoginForBemSucedido() {
        var loginDto = new AuthenticationRequestDto();
        loginDto.setUsername("user");
        loginDto.setPassword("pass");
        var user = new UserData();
        user.setUsername("user");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(user)).thenReturn("mock.jwt.token");

        AuthenticationResponseDto response = authenticationService.login(loginDto);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, times(1)).generateToken(user);
    }
}