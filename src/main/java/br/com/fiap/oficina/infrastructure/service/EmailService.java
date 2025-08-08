package br.com.fiap.oficina.infrastructure.service;

import br.com.fiap.oficina.domain.model.ServiceOrder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendServiceOrderBudget(ServiceOrder order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@oficina.com");
        message.setTo("email-do-cliente-para-teste@exemplo.com");
        message.setSubject("Orçamento da sua Ordem de Serviço #" + order.getId());

        String text = """
            Olá, %s!

            O diagnóstico do seu veículo %s (%s) foi concluído.

            O orçamento total é de R$ %.2f.

            Aguardamos a sua aprovação para iniciar o serviço.

            Atenciosamente,
            Equipe da Oficina
            """.formatted(
                order.getClient().getName(),
                order.getVehicle().getModel(),
                order.getVehicle().getPlate(),
                order.getTotalAmount()
        );
        message.setText(text);

        mailSender.send(message);
    }
}