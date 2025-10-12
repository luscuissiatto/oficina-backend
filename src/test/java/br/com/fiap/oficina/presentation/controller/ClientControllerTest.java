package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ClientRequestDto;
import br.com.fiap.oficina.application.service.ClientService;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.infrastructure.persistence.dao.UserDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import br.com.fiap.oficina.infrastructure.security.TokenService;
import br.com.fiap.oficina.infrastructure.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDao userDao;
    @MockBean
    private ClientService clientService;
    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        UserData testUser = new UserData();
        testUser.setUsername("testuser_client");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    public void deveCriarClienteComSucesso() throws Exception {
        var requestDto = new ClientRequestDto();
        requestDto.setName("Cliente Valido de Teste");
        requestDto.setDocument("00000000000");
        Client clientCriado = new Client();
        clientCriado.setId(1L);
        clientCriado.setName("Cliente Valido de Teste");
        clientCriado.setDocument("00000000000");

        when(clientService.create(any(ClientRequestDto.class))).thenReturn(clientCriado);

        mockMvc.perform(post("/clients")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cliente Valido de Teste"));
    }

    @Test
    public void deveListarTodosOsClientes() throws Exception {
        Client clientA = new Client("Cliente A", "11111111111");
        Client clientB = new Client("Cliente B", "22222222222");
        when(clientService.findAllByDocument(null)).thenReturn(List.of(clientA, clientB));

        mockMvc.perform(get("/clients")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Cliente A"));
    }

    @Test
    public void deveDeletarClienteComSucesso() throws Exception {
        long clientId = 1L;
        doNothing().when(clientService).delete(clientId);

        mockMvc.perform(delete("/clients/" + clientId)
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deveRetornarNotFoundAoBuscarClienteInexistente() throws Exception {
        long idInexistente = 999L;
        when(clientService.findById(idInexistente)).thenThrow(new ResponseStatusException(NOT_FOUND));

        mockMvc.perform(get("/clients/" + idInexistente)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
}