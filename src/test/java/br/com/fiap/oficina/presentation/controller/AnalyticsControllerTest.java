package br.com.fiap.oficina.presentation.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
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

    private String authToken;

    @BeforeEach
    void setUp() {
        UserData testUser = new UserData();
        testUser.setUsername("testuser_analytics");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveRetornarTempoMedioDeExecucaoCorretamente() throws Exception {
        Client client = clientRepository.save(new Client("Cliente Analytics", "11111111111"));
        Vehicle vehicle = vehicleRepository.save(new Vehicle("ANT-2025", "Marca", "Modelo", 2025, client));
        Service oilChangeTest = serviceRepository.save(new Service("Troca de Óleo Teste", "Desc", new BigDecimal("100")));
        Service brakesTest = serviceRepository.save(new Service("Revisão de Freios Teste", "Desc", new BigDecimal("200")));

        // OS 1: Demorou 2 horas
        ServiceOrder order1 = new ServiceOrder();
        order1.setClient(client);
        order1.setVehicle(vehicle);
        order1.setServices(List.of(oilChangeTest));
        order1.setItems(List.of());
        order1.setStatus(OrderStatus.COMPLETED);
        order1.setCreationDate(LocalDateTime.now().minusHours(10));
        order1.setFinishDate(LocalDateTime.now().minusHours(8));
        order1.calculateTotalAmount();
        serviceOrderRepository.save(order1);

        // OS 2: Demorou 4 horas
        ServiceOrder order2 = new ServiceOrder();
        order2.setClient(client);
        order2.setVehicle(vehicle);
        order2.setServices(List.of(oilChangeTest));
        order2.setItems(List.of());
        order2.setStatus(OrderStatus.DELIVERED);
        order2.setCreationDate(LocalDateTime.now().minusHours(6));
        order2.setFinishDate(LocalDateTime.now().minusHours(2));
        order2.calculateTotalAmount();
        serviceOrderRepository.save(order2);

        // OS 3: Demorou 5 horas
        ServiceOrder order3 = new ServiceOrder();
        order3.setClient(client);
        order3.setVehicle(vehicle);
        order3.setServices(List.of(brakesTest));
        order3.setItems(List.of());
        order3.setStatus(OrderStatus.COMPLETED);
        order3.setCreationDate(LocalDateTime.now().minusHours(10));
        order3.setFinishDate(LocalDateTime.now().minusHours(5));
        order3.calculateTotalAmount();
        serviceOrderRepository.save(order3);

        mockMvc.perform(get("/analytics/average-service-time")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].serviceName").value("Troca de Óleo Teste"))
                .andExpect(jsonPath("$[0].averageTimeInHours").value(3.0))
                .andExpect(jsonPath("$[1].serviceName").value("Revisão de Freios Teste"))
                .andExpect(jsonPath("$[1].averageTimeInHours").value(5.0));
    }
}