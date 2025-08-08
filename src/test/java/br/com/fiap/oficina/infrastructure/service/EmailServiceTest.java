package br.com.fiap.oficina.infrastructure.service;

import br.com.fiap.oficina.domain.model.Client;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.model.Vehicle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private ServiceOrder testOrder;

    @BeforeEach
    void setUp() {
        Client client = new Client("Cliente Teste de Email", "12345678901");
        Vehicle vehicle = new Vehicle("EMAIL-001", "Marca Teste", "Modelo Email", 2025, client);

        testOrder = new ServiceOrder();
        testOrder.setId(99L);
        testOrder.setClient(client);
        testOrder.setVehicle(vehicle);
        testOrder.setTotalAmount(new BigDecimal("550.75"));
    }

    @Test
    void deveEnviarEmailComOsDadosCorretos() {
        emailService.sendServiceOrderBudget(testOrder);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        Assertions.assertNotNull(sentMessage.getTo());
        assertEquals("email-do-cliente-para-teste@exemplo.com", sentMessage.getTo()[0]);
        assertEquals("Orçamento da sua Ordem de Serviço #99", sentMessage.getSubject());

        String expectedText = """
            Olá, Cliente Teste de Email!

            O diagnóstico do seu veículo Modelo Email (EMAIL-001) foi concluído.

            O orçamento total é de R$ 550,75.

            Aguardamos a sua aprovação para iniciar o serviço.

            Atenciosamente,
            Equipe da Oficina
            """;
        Assertions.assertNotNull(sentMessage.getText());
        assertEquals(expectedText.trim(), sentMessage.getText().trim());
    }
}