package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ServiceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;
    @Autowired private ServiceRepository serviceRepository;
    @MockBean private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        UserData testUser = new UserData();
        testUser.setUsername("testuser_service");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveCriarServicoComSucesso() throws Exception {
        Service newService = new Service("Alinhamento", "3D", new BigDecimal("150.00"));

        mockMvc.perform(post("/services")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alinhamento"));
    }

    @Test
    void deveListarTodosOsServicos() throws Exception {
        serviceRepository.save(new Service("Troca de Óleo", "5W30", new BigDecimal("250.00")));

        mockMvc.perform(get("/services")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveBuscarServicoPorId() throws Exception {
        Service savedService = serviceRepository.save(new Service("Freios", "ABS", new BigDecimal("300.00")));

        mockMvc.perform(get("/services/" + savedService.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedService.getId()));
    }

    @Test
    void deveAtualizarServicoComSucesso() throws Exception {
        Service savedService = serviceRepository.save(new Service("Revisão", "Básica", new BigDecimal("200.00")));
        Service updatedInfo = new Service("Revisão Completa", "50 itens", new BigDecimal("500.00"));

        mockMvc.perform(put("/services/" + savedService.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Revisão Completa"))
                .andExpect(jsonPath("$.price").value(500.00));
    }

    @Test
    void deveDeletarServicoComSucesso() throws Exception {
        Service serviceToDelete = serviceRepository.save(new Service("Limpeza", "TBI", new BigDecimal("90.00")));

        mockMvc.perform(delete("/services/" + serviceToDelete.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarNotFoundAoTentarDeletarServicoInexistente() throws Exception {
        long idInexistente = 999L;

        mockMvc.perform(delete("/services/" + idInexistente)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
}