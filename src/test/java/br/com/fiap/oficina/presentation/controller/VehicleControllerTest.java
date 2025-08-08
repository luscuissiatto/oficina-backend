package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.dto.VehicleRequestDto;
import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.UserDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import br.com.fiap.oficina.infrastructure.security.TokenService;
import br.com.fiap.oficina.infrastructure.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerTest {

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

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockBean
    private EmailService emailService;

    private String authToken;
    private Client savedClient;

    @BeforeEach
    void setUp() {
        savedClient = clientRepository.save(new Client("Dono do Veículo", "00000000001"));

        UserData testUser = new UserData();
        testUser.setUsername("testuser_vehicle");
        userDao.save(testUser);
        authToken = "Bearer " + tokenService.generateToken(testUser);
    }

    @Test
    void deveCriarVeiculoComSucesso() throws Exception {
        VehicleRequestDto newVehicleDto = new VehicleRequestDto();
        newVehicleDto.setClientId(savedClient.getId());
        newVehicleDto.setPlate("BRA2E19"); // Formato Mercosul válido
        newVehicleDto.setBrand("Honda");
        newVehicleDto.setModel("Civic");
        newVehicleDto.setYear(2023);

        mockMvc.perform(post("/vehicles")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.plate").value("BRA2E19"))
                .andExpect(jsonPath("$.client.id").value(savedClient.getId()));
    }

    @Test
    void deveFalharAoCriarVeiculoComPlacaInvalida() throws Exception {
        VehicleRequestDto newVehicleWithInvalidPlate = new VehicleRequestDto();
        newVehicleWithInvalidPlate.setClientId(savedClient.getId());
        newVehicleWithInvalidPlate.setPlate("ABC-123"); // Formato antigo inválido (falta um dígito)
        newVehicleWithInvalidPlate.setBrand("Marca");
        newVehicleWithInvalidPlate.setModel("Modelo");
        newVehicleWithInvalidPlate.setYear(2020);

        mockMvc.perform(post("/vehicles")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicleWithInvalidPlate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.plate").value("Formato de placa inválido"));
    }

    @Test
    void deveListarVeiculosPorCliente() throws Exception {
        vehicleRepository.save(new Vehicle("AAA1A11", "VW", "Nivus", 2023, savedClient));
        vehicleRepository.save(new Vehicle("BBB2B22", "VW", "T-Cross", 2024, savedClient));

        mockMvc.perform(get("/vehicles/client/" + savedClient.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveBuscarVeiculoPorId() throws Exception {
        Vehicle savedVehicle = vehicleRepository.save(new Vehicle("CCC3C33", "Fiat", "Pulse", 2023, savedClient));

        mockMvc.perform(get("/vehicles/vehicle/" + savedVehicle.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedVehicle.getId()));
    }

    @Test
    void deveAtualizarVeiculoComSucesso() throws Exception {
        Vehicle savedVehicle = vehicleRepository.save(new Vehicle("DDD4D44", "Chevrolet", "Tracker", 2022, savedClient));

        VehicleRequestDto updatedDto = new VehicleRequestDto();
        updatedDto.setClientId(savedClient.getId());
        updatedDto.setPlate("DDD4D44");
        updatedDto.setBrand("Chevrolet");
        updatedDto.setModel("Tracker RS"); // Modelo atualizado
        updatedDto.setYear(2024); // Ano atualizado

        mockMvc.perform(put("/vehicles/" + savedVehicle.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Tracker RS"))
                .andExpect(jsonPath("$.year").value(2024));
    }

    @Test
    void deveDeletarVeiculoComSucesso() throws Exception {
        Vehicle vehicleToDelete = vehicleRepository.save(new Vehicle("EEE5E55", "Jeep", "Renegade", 2021, savedClient));

        mockMvc.perform(delete("/vehicles/" + vehicleToDelete.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}