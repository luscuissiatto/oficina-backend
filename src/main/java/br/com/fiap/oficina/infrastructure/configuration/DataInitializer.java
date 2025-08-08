package br.com.fiap.oficina.infrastructure.configuration;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.OrderItem;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.Part;
import br.com.fiap.oficina.domain.model.Service;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.model.Vehicle;
import br.com.fiap.oficina.domain.repository.ClientRepository;
import br.com.fiap.oficina.domain.repository.PartRepository;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import br.com.fiap.oficina.domain.repository.ServiceRepository;
import br.com.fiap.oficina.domain.repository.VehicleRepository;
import br.com.fiap.oficina.infrastructure.persistence.dao.UserDao;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final PartRepository partRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    @Value("${app.admin.default-password}")
    private String adminPassword;

    public DataInitializer(UserDao userDao, PasswordEncoder passwordEncoder, ClientRepository clientRepository, VehicleRepository vehicleRepository, ServiceRepository serviceRepository, PartRepository partRepository, ServiceOrderRepository serviceOrderRepository) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.partRepository = partRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userDao.findByUsername("admin").isEmpty()) {
            UserData adminUser = new UserData();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            userDao.save(adminUser);
            logger.info(">>> Created admin user from environment configuration.");
        }

        if (clientRepository.findAll().isEmpty()) {
            logger.info(">>> Pre-populating database with a larger set of initial data...");

            List<Service> services = createServices();
            List<Part> parts = createParts();
            List<Client> clients = createClients();
            List<Vehicle> vehicles = createVehicles(clients);

            createServiceOrders(vehicles, services, parts);

            logger.info(">>> Larger initial dataset created successfully!");
        }
    }

    private List<Service> createServices() {
        List<Service> serviceList = List.of(
                new Service("Troca de Óleo", "Troca de óleo do motor e filtro.", new BigDecimal("250.00")),
                new Service("Alinhamento e Balanceamento", "Alinhamento 3D e balanceamento.", new BigDecimal("150.00")),
                new Service("Troca de Pneus", "Substituição dos pneus.", new BigDecimal("80.00")),
                new Service("Revisão Completa", "Check-up de 50 itens do veículo.", new BigDecimal("500.00")),
                new Service("Freios", "Verificação e troca de pastilhas de freio.", new BigDecimal("300.00")),
                new Service("Diagnóstico Eletrônico", "Leitura de scanner e diagnóstico de falhas.", new BigDecimal("120.00"))
        );

        List<Service> savedServices = new ArrayList<>();
        serviceList.forEach(service -> savedServices.add(serviceRepository.save(service)));
        return savedServices;
    }

    private List<Part> createParts() {
        Random rand = new SecureRandom();
        List<Part> partList = List.of(
                new Part("Filtro de Óleo", "Filtro de óleo para motores 1.6", new BigDecimal("45.00"), rand.nextInt(50)),
                new Part("Pneu Aro 15", "Pneu 185/60 R15", new BigDecimal("350.00"), rand.nextInt(20)),
                new Part("Pastilha de Freio", "Jogo de pastilhas dianteiras", new BigDecimal("95.00"), rand.nextInt(30)),
                new Part("Óleo 5W30", "Óleo sintético 5W30 (Litro)", new BigDecimal("60.00"), rand.nextInt(100)),
                new Part("Vela de Ignição", "Jogo com 4 velas", new BigDecimal("85.00"), rand.nextInt(40)),
                new Part("Bateria 60A", "Bateria automotiva 60 Amperes", new BigDecimal("450.00"), rand.nextInt(10))
        );

        List<Part> savedParts = new ArrayList<>();
        partList.forEach(part -> savedParts.add(partRepository.save(part)));
        return savedParts;
    }

    private List<Client> createClients() {
        List<Client> clientList = List.of(
                new Client("João da Silva", "11122233344"),
                new Client("Maria Oliveira", "22233344455"),
                new Client("Carlos Pereira", "33344455566"),
                new Client("Ana Costa", "44455566677"),
                new Client("Pedro Martins", "55566677788")
        );
        List<Client> savedClients = new ArrayList<>();
        clientList.forEach(client -> savedClients.add(clientRepository.save(client)));
        return savedClients;
    }

    private List<Vehicle> createVehicles(List<Client> clients) {
        List<Vehicle> vehicleList = List.of(
                new Vehicle("ABC1D23", "Volkswagen", "Gol", 2022, clients.get(0)),
                new Vehicle("DEF4E56", "Chevrolet", "Onix", 2021, clients.get(0)),
                new Vehicle("GHI7F89", "Fiat", "Mobi", 2023, clients.get(1)),
                new Vehicle("JKL0A12", "Ford", "Ka", 2020, clients.get(2)),
                new Vehicle("MNO3B45", "Hyundai", "HB20", 2022, clients.get(3)),
                new Vehicle("PQR6C78", "Toyota", "Corolla", 2023, clients.get(4)),
                new Vehicle("STU9D01", "Honda", "Civic", 2021, clients.get(4))
        );
        List<Vehicle> savedVehicles = new ArrayList<>();
        vehicleList.forEach(vehicle -> savedVehicles.add(vehicleRepository.save(vehicle)));
        return savedVehicles;
    }

    private void createServiceOrders(List<Vehicle> vehicles, List<Service> services, List<Part> parts) {
        Random rand = new SecureRandom();
        OrderStatus[] statuses = OrderStatus.values();

        for (int i = 0; i < 8; i++) {
            Vehicle randomVehicle = vehicles.get(rand.nextInt(vehicles.size()));
            Client vehicleOwner = randomVehicle.getClient();

            ServiceOrder order = new ServiceOrder();
            order.setClient(vehicleOwner);
            order.setVehicle(randomVehicle);
            order.setCreationDate(LocalDateTime.now().minusDays(rand.nextInt(30)));

            order.setServices(List.of(services.get(rand.nextInt(services.size())), services.get(rand.nextInt(services.size()))));

            OrderItem item1 = new OrderItem(parts.get(rand.nextInt(parts.size())), rand.nextInt(4) + 1);
            OrderItem item2 = new OrderItem(parts.get(rand.nextInt(parts.size())), rand.nextInt(2) + 1);
            order.setItems(List.of(item1, item2));

            OrderStatus randomStatus = statuses[rand.nextInt(statuses.length)];
            order.setStatus(randomStatus);

            if(randomStatus == OrderStatus.COMPLETED || randomStatus == OrderStatus.DELIVERED){
                order.setFinishDate(order.getCreationDate().plusDays(rand.nextInt(5) + 1L));
            }

            order.calculateTotalAmount();
            serviceOrderRepository.save(order);
        }
    }
}