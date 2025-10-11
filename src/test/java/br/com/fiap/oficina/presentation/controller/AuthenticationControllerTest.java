package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.AuthenticationRequestDto;
import br.com.fiap.oficina.application.dto.AuthenticationResponseDto;
import br.com.fiap.oficina.application.service.AuthenticationService; // Importamos o service
import br.com.fiap.oficina.infrastructure.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Importamos o MockBean
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Usamos @MockBean para substituir a implementação real do service por um mock
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private EmailService emailService;

    @Test
    public void deveAutenticarComCredenciaisValidasERetornarToken() throws Exception {
        // Arrange
        var loginRequest = new AuthenticationRequestDto();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("test_password");

        var responseDto = new AuthenticationResponseDto("fake.jwt.token");

        // Configuramos o mock do service
        when(authenticationService.login(any(AuthenticationRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"));
    }

    @Test
    public void deveFalharAutenticacaoComCredenciaisInvalidas() throws Exception {
        // Arrange
        var loginRequest = new AuthenticationRequestDto();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("wrong_password");

        // Configuramos o mock para lançar a exceção que o Spring Security lançaria
        when(authenticationService.login(any(AuthenticationRequestDto.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden()); // ou .isForbidden() dependendo da sua config de segurança
    }
}