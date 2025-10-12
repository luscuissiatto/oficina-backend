package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.ServiceOrderRequestDto;
import br.com.fiap.oficina.application.dto.ServiceOrderSummaryDto;
import br.com.fiap.oficina.application.dto.UpdateStatusRequestDto;
import br.com.fiap.oficina.application.service.ServiceOrderService;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.ServiceOrder;
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
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ServiceOrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;

    @MockBean
    private ServiceOrderService serviceOrderService;

    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        UserData testUser = new UserData();
        testUser.setUsername("testuser_order");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    public void deveCriarOrdemDeServicoComSucesso() throws Exception {
        var requestDto = new ServiceOrderRequestDto();
        ServiceOrder orderCriada = new ServiceOrder();
        orderCriada.setId(1L);
        orderCriada.setStatus(OrderStatus.RECEIVED);

        when(serviceOrderService.create(any(ServiceOrderRequestDto.class))).thenReturn(orderCriada);

        mockMvc.perform(post("/service-orders")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    public void deveAtualizarStatusComSucesso() throws Exception {
        var requestDto = new UpdateStatusRequestDto();
        requestDto.setNewStatus("IN_PROGRESS");
        ServiceOrder orderAtualizada = new ServiceOrder();
        orderAtualizada.setId(1L);
        orderAtualizada.setStatus(OrderStatus.IN_PROGRESS);

        when(serviceOrderService.updateStatus(anyLong(), any(UpdateStatusRequestDto.class))).thenReturn(orderAtualizada);

        mockMvc.perform(patch("/service-orders/1/status")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void deveListarOrdensDeServicoComSucesso() throws Exception {
        var summaryDto = new ServiceOrderSummaryDto(1L, "Cliente Teste", "ABC-1234", OrderStatus.IN_PROGRESS, BigDecimal.TEN, LocalDateTime.now());
        when(serviceOrderService.findAllSummaries()).thenReturn(Collections.singletonList(summaryDto));

        mockMvc.perform(get("/service-orders")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName").value("Cliente Teste"));
    }
}