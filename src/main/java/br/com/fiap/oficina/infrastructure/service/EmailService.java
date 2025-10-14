package br.com.fiap.oficina.infrastructure.service;

import br.com.fiap.oficina.domain.model.ServiceOrder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusUpdateNotification(ServiceOrder order) {
        String subject = "";
        String text = "";

        switch (order.getStatus()) {
            case AWAITING_APPROVAL:
                subject = "Orçamento da sua Ordem de Serviço #" + order.getId();
                text = """
                    Olá, %s!
                    O diagnóstico do seu veículo %s (%s) foi concluído.
                    O orçamento total é de R$ %.2f.
                    Aguardamos a sua aprovação para iniciar o serviço.
                    """.formatted(
                        order.getClient().getName(),
                        order.getVehicle().getModel(),
                        order.getVehicle().getPlate(),
                        order.getTotalAmount()
                );
                break;

            case IN_PROGRESS:
                subject = "Sua Ordem de Serviço #" + order.getId() + " está em execução!";
                text = "Olá, %s!\n\nSeu orçamento foi aprovado e o serviço no seu veículo %s (%s) já foi iniciado.".formatted(
                        order.getClient().getName(),
                        order.getVehicle().getModel(),
                        order.getVehicle().getPlate()
                );
                break;

            case COMPLETED:
                subject = "Serviço Concluído! Ordem de Serviço #" + order.getId();
                text = "Olá, %s!\n\nBoas notícias! O serviço no seu veículo %s (%s) foi finalizado. O valor total é de R$ %.2f. Você já pode vir retirá-lo.".formatted(
                        order.getClient().getName(),
                        order.getVehicle().getModel(),
                        order.getVehicle().getPlate(),
                        order.getTotalAmount()
                );
                break;

            case REFUSED:
                subject = "Orçamento Recusado - Ordem de Serviço #" + order.getId();
                text = "Olá, %s!\n\nRecebemos a sua recusa do orçamento para o serviço no veículo %s (%s). Por favor, entre em contato para agendar a retirada do veículo.".formatted(
                        order.getClient().getName(),
                        order.getVehicle().getModel(),
                        order.getVehicle().getPlate()
                );
                break;

            default:
                return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@oficina.com");
        message.setTo("email-do-cliente-para-teste@exemplo.com");
        message.setSubject(subject);
        message.setText(text + "\n\nAtenciosamente,\nEquipe da Oficina");

        mailSender.send(message);
    }
}