package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.application.service.VehicleService;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TokenService tokenService;
    @Autowired private UserDao userDao;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private EmailService emailService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        UserData testUser = new UserData();
        testUser.setUsername("testuser_vehicle");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    public void deveCriarVeiculoComSucesso() throws Exception {
        var client = new Client("Cliente Teste", "123");
        client.setId(1L);

        var requestDto = new VehicleRequestDto();
        requestDto.setBrand("Marca");
        requestDto.setModel("Modelo");
        requestDto.setYear(2023);
        requestDto.setPlate("ABC-1234");
        requestDto.setClientId(client.getId());

        Vehicle vehicleCriado = new Vehicle(requestDto.getPlate(), requestDto.getBrand(), requestDto.getModel(), requestDto.getYear(), client);
        vehicleCriado.setId(10L);

        when(vehicleService.create(any(VehicleRequestDto.class))).thenReturn(vehicleCriado);

        mockMvc.perform(post("/vehicles")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.plate").value("ABC-1234"));
    }

    @Test
    public void deveListarVeiculosDeUmCliente() throws Exception {
        long clientId = 1L;
        var client = new Client("Cliente Teste", "123");
        client.setId(clientId);
        var vehicle = new Vehicle("DEF-5678", "Outra Marca", "Outro Modelo", 2022, client);

        when(vehicleService.findByClientId(clientId)).thenReturn(Collections.singletonList(vehicle));

        mockMvc.perform(get("/vehicles/client/" + clientId)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plate").value("DEF-5678"));
    }
}