package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ServiceRequestDto;
import br.com.fiap.oficina.application.service.ServiceService;
import br.com.fiap.oficina.domain.model.Service;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;

    @MockBean
    private ServiceService serviceService;

    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        UserData testUser = new UserData();
        testUser.setUsername("testuser_service");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    public void deveCriarServicoComSucesso() throws Exception {
        var requestDto = new ServiceRequestDto("Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        var serviceCriado = new Service("Troca de Óleo", "Troca completa", new BigDecimal("150.00"));
        serviceCriado.setId(1L);

        when(serviceService.create(any(ServiceRequestDto.class))).thenReturn(serviceCriado);

        mockMvc.perform(post("/services")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Troca de Óleo"));
    }

    @Test
    public void deveListarServicos() throws Exception {
        var service = new Service("Alinhamento", "Alinhar rodas", BigDecimal.TEN);
        when(serviceService.findAll()).thenReturn(Collections.singletonList(service));

        mockMvc.perform(get("/services")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alinhamento"));
    }

    @Test
    public void deveDeletarServicoComSucesso() throws Exception {
        long serviceId = 1L;
        doNothing().when(serviceService).delete(serviceId);

        mockMvc.perform(delete("/services/" + serviceId)
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}