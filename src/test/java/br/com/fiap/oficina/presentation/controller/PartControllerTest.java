package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.PartRequestDto;
import br.com.fiap.oficina.application.service.PartService;
import br.com.fiap.oficina.domain.model.Part;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;

    @MockBean
    private PartService partService;

    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        UserData testUser = new UserData();
        testUser.setUsername("testuser_part");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    public void deveCriarPecaComSucesso() throws Exception {
        var requestDto = new PartRequestDto("Vela de Ignição", "NGK", new BigDecimal("75.50"), 50);
        Part partCriada = new Part("Vela de Ignição", "NGK", new BigDecimal("75.50"), 50);
        partCriada.setId(1L);

        when(partService.create(any(PartRequestDto.class))).thenReturn(partCriada);

        mockMvc.perform(post("/parts")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vela de Ignição"));
    }

    @Test
    public void deveListarTodasAsPecas() throws Exception {
        Part pneu = new Part("Pneu", "Aro 15", new BigDecimal("350.00"), 20);
        when(partService.findAll()).thenReturn(List.of(pneu));

        mockMvc.perform(get("/parts")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveDeletarPecaComSucesso() throws Exception {
        long partId = 1L;
        doNothing().when(partService).delete(partId);

        mockMvc.perform(delete("/parts/" + partId)
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}