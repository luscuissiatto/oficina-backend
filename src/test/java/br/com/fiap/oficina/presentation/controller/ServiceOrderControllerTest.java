package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.AddItemsToOrderRequestDto;
import br.com.fiap.oficina.application.dto.OrderItemRequestDto;
import br.com.fiap.oficina.application.dto.ServiceOrderRequestDto;
import br.com.fiap.oficina.application.dto.UpdateStatusRequestDto;
import br.com.fiap.oficina.domain.model.*;
import br.com.fiap.oficina.domain.repository.*;
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
import static org.hamcrest.Matchers.hasSize;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.oficina.util.BigDecimalMatcher.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ServiceOrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClientRepository clientRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private PartRepository partRepository;
    @Autowired private ServiceOrderRepository serviceOrderRepository;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;
    @MockBean private EmailService emailService;

    private Client savedClient;
    private Vehicle savedVehicle;
    private Service savedService;
    private String authToken;

    @BeforeEach
    void setUp() {
        savedClient = clientRepository.save(new Client("Cliente Teste", "12345678901"));
        savedVehicle = vehicleRepository.save(new Vehicle("TST-1234", "Marca Teste", "Modelo Teste", 2025, savedClient));
        savedService = serviceRepository.save(new Service("Serviço Teste", "Desc Teste", new BigDecimal("100.00")));

        UserData testUser = new UserData();
        testUser.setUsername("testuser");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveBuscarOrdemDeServicoPorId() throws Exception {
        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setStatus(OrderStatus.RECEIVED);
        order.setCreationDate(LocalDateTime.now());
        order.setServices(List.of());
        order.setItems(List.of());
        order.calculateTotalAmount();
        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        mockMvc.perform(get("/service-orders/" + savedOrder.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.client.name").value(savedClient.getName()));
    }

    @Test
    void deveListarTodasAsOrdensDeServico() throws Exception {
        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setStatus(OrderStatus.RECEIVED);
        order.setCreationDate(LocalDateTime.now());
        order.setServices(List.of());
        order.setItems(List.of());
        order.calculateTotalAmount();
        serviceOrderRepository.save(order);

        mockMvc.perform(get("/service-orders")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName").value(savedClient.getName()));
    }

    @Test
    void deveAdicionarItensAOrdemDeServicoExistente() throws Exception {
        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setStatus(OrderStatus.RECEIVED);
        order.setCreationDate(LocalDateTime.now());

        order.setServices(new ArrayList<>());
        order.setItems(new ArrayList<>());

        order.calculateTotalAmount();
        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        Part newPart = partRepository.save(new Part("Peça Nova", "Desc", new BigDecimal("75.00"), 10));
        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setPartId(newPart.getId());
        itemDto.setQuantity(1);

        AddItemsToOrderRequestDto addItemsDto = new AddItemsToOrderRequestDto();
        addItemsDto.setItems(new ArrayList<>(List.of(itemDto)));
        addItemsDto.setServiceIds(new ArrayList<>(List.of(savedService.getId())));

        mockMvc.perform(patch("/service-orders/" + savedOrder.getId() + "/add-items")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(comparesEqualTo(new BigDecimal("175.00"))));
    }

    @Test
    void deveConsultarStatusParaClienteComSucesso() throws Exception {
        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCreationDate(LocalDateTime.now());
        order.setServices(List.of());
        order.setItems(List.of());
        order.calculateTotalAmount();

        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        mockMvc.perform(get("/service-orders/status")
                        .param("document", savedClient.getDocument())
                        .param("plate", savedVehicle.getPlate())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(savedOrder.getId()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deveCriarOrdemDeServicoComSucesso() throws Exception {
        Part savedPart = partRepository.save(new Part("Peça Teste", "Desc Peça Teste", new BigDecimal("50.00"), 10));

        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setPartId(savedPart.getId());
        itemDto.setQuantity(2);

        ServiceOrderRequestDto requestDto = new ServiceOrderRequestDto();
        requestDto.setClientId(savedClient.getId());
        requestDto.setVehicleId(savedVehicle.getId());
        requestDto.setServiceIds(List.of(savedService.getId()));
        requestDto.setItems(List.of(itemDto));

        mockMvc.perform(post("/service-orders")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalAmount").value(comparesEqualTo(new BigDecimal("200.00"))));
    }

    @Test
    void deveAtualizarStatusEAbaterEstoqueComSucesso() throws Exception {
        Part partComEstoque = partRepository.save(new Part("Peça com Estoque", "Desc", new BigDecimal("25.00"), 5));

        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setCreationDate(LocalDateTime.now());
        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        order.setServices(List.of(savedService));
        order.setItems(List.of(new OrderItem(partComEstoque, 2)));
        order.calculateTotalAmount();
        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        UpdateStatusRequestDto statusDto = new UpdateStatusRequestDto();
        statusDto.setNewStatus("IN_PROGRESS");

        mockMvc.perform(patch("/service-orders/" + savedOrder.getId() + "/status")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        Part partAtualizada = partRepository.findById(partComEstoque.getId()).get();
        assertEquals(3, partAtualizada.getStockQuantity()); // 5 - 2 = 3
    }

    @Test
    void deveFalharAoAtualizarStatusPorFaltaDeEstoque() throws Exception {
        Part partSemEstoque = partRepository.save(new Part("Peça sem Estoque", "Desc", new BigDecimal("30.00"), 1));

        ServiceOrder order = new ServiceOrder();
        order.setClient(savedClient);
        order.setVehicle(savedVehicle);
        order.setCreationDate(LocalDateTime.now());
        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        order.setItems(List.of(new OrderItem(partSemEstoque, 2)));
        order.setServices(List.of());
        order.calculateTotalAmount();
        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        UpdateStatusRequestDto statusDto = new UpdateStatusRequestDto();
        statusDto.setNewStatus("IN_PROGRESS");

        mockMvc.perform(patch("/service-orders/" + savedOrder.getId() + "/status")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveListarOrdensDeServicoPorCliente() throws Exception {
        ServiceOrder order1 = new ServiceOrder();
        order1.setClient(savedClient);
        order1.setVehicle(savedVehicle);
        order1.setStatus(OrderStatus.RECEIVED);
        order1.setCreationDate(LocalDateTime.now());
        order1.setServices(List.of());
        order1.setItems(List.of());
        order1.calculateTotalAmount();
        serviceOrderRepository.save(order1);

        ServiceOrder order2 = new ServiceOrder();
        order2.setClient(savedClient);
        order2.setVehicle(savedVehicle);
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setCreationDate(LocalDateTime.now());
        order2.setServices(List.of());
        order2.setItems(List.of());
        order2.calculateTotalAmount();
        serviceOrderRepository.save(order2);

        mockMvc.perform(get("/service-orders/client/" + savedClient.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}