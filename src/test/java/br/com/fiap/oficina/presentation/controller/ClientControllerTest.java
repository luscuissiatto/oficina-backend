package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.repository.ClientRepository;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ClientRepository clientRepository;

    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        UserData testUser = new UserData();
        testUser.setUsername("testuser_client");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveCriarClienteComSucesso() throws Exception {
        Client newClient = new Client("Cliente Valido de Teste", "00000000000");

        mockMvc.perform(post("/clients")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Cliente Valido de Teste"));
    }

    @Test
    void deveFalharAoCriarClienteComDocumentoInvalido() throws Exception {
        Client newClientWithInvalidDoc = new Client("Cliente Invalido", "123");

        mockMvc.perform(post("/clients")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientWithInvalidDoc)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.document").value("Documento (CPF/CNPJ) inválido"));
    }

    @Test
    void deveListarTodosOsClientes() throws Exception {
        clientRepository.save(new Client("Cliente A", "11111111111"));
        clientRepository.save(new Client("Cliente B", "22222222222"));

        mockMvc.perform(get("/clients")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Cliente A"))
                .andExpect(jsonPath("$[1].name").value("Cliente B"));
    }

    @Test
    void deveBuscarClientePorDocumento() throws Exception {
        clientRepository.save(new Client("Cliente C", "33333333333"));

        mockMvc.perform(get("/clients")
                        .param("document", "33333333333")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].document").value("33333333333"));
    }

    @Test
    void deveAtualizarClienteComSucesso() throws Exception {
        Client savedClient = clientRepository.save(new Client("Cliente Original", "44444444444"));
        Client updatedInfo = new Client("Cliente com Nome Atualizado", "44444444444");

        mockMvc.perform(put("/clients/" + savedClient.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedClient.getId()))
                .andExpect(jsonPath("$.name").value("Cliente com Nome Atualizado"));
    }

    @Test
    void deveDeletarClienteComSucesso() throws Exception {
        Client clientToDelete = clientRepository.save(new Client("Cliente a Deletar", "55555555555"));

        mockMvc.perform(delete("/clients/" + clientToDelete.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarNotFoundAoBuscarClienteInexistente() throws Exception {
        long idInexistente = 999L;

        mockMvc.perform(get("/clients/" + idInexistente)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
}