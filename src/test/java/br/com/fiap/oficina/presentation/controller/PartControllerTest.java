package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.repository.PartRepository;
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
class PartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;
    @Autowired private PartRepository partRepository;
    @MockBean private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        UserData testUser = new UserData();
        testUser.setUsername("testuser_part");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveCriarPecaComSucesso() throws Exception {
        Part newPart = new Part("Vela de Ignição", "NGK", new BigDecimal("75.50"), 50);

        mockMvc.perform(post("/parts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPart)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vela de Ignição"));
    }

    @Test
    void deveListarTodasAsPecas() throws Exception {
        partRepository.save(new Part("Pneu", "Aro 15", new BigDecimal("350.00"), 20));

        mockMvc.perform(get("/parts")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveBuscarPecaPorId() throws Exception {
        Part savedPart = partRepository.save(new Part("Óleo 5W30", "Sintético", new BigDecimal("60.00"), 100));

        mockMvc.perform(get("/parts/" + savedPart.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPart.getId()));
    }

    @Test
    void deveAtualizarPecaComSucesso() throws Exception {
        Part savedPart = partRepository.save(new Part("Filtro", "Original", new BigDecimal("40.00"), 30));
        Part updatedInfo = new Part("Filtro de Ar", "Original K&N", new BigDecimal("120.00"), 25);

        mockMvc.perform(put("/parts/" + savedPart.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Filtro de Ar"))
                .andExpect(jsonPath("$.stockQuantity").value(25));
    }

    @Test
    void deveDeletarPecaComSucesso() throws Exception {
        Part partToDelete = partRepository.save(new Part("Bateria", "60A", new BigDecimal("400.00"), 10));

        mockMvc.perform(delete("/parts/" + partToDelete.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}