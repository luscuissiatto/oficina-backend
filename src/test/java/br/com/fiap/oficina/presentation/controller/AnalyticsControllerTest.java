package br.com.fiap.oficina.presentation.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.application.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.UserDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import br.com.fiap.oficina.infrastructure.security.TokenService;
import br.com.fiap.oficina.infrastructure.service.EmailService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AnalyticsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;
    @Autowired private ClientRepository clientRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private ServiceOrderRepository serviceOrderRepository;
    @MockBean
    private EmailService emailService;

    @MockBean // Usa @MockBean para substituir o Service real por um mock
    private AnalyticsService analyticsService;

    private String authToken;

    @BeforeEach
    void setUp() {
        UserData testUser = new UserData();
        testUser.setUsername("testuser_analytics");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveRetornarHttpOkComJsonCorreto() throws Exception {
        // Arrange - Preparamos a resposta que o service DEVE retornar
        List<AverageServiceTimeDto> mockResponse = List.of(
                new AverageServiceTimeDto("Troca de Óleo Teste", 3.0),
                new AverageServiceTimeDto("Revisão de Freios Teste", 5.0)
        );
        when(analyticsService.getAverageServiceTime()).thenReturn(mockResponse);

        // Act & Assert - Testamos a camada web (Controller)
        mockMvc.perform(get("/analytics/average-service-time")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].serviceName").value("Troca de Óleo Teste"))
                .andExpect(jsonPath("$[0].averageTimeInHours").value(3.0));
    }
}