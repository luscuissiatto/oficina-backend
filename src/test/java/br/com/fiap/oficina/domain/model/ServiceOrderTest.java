package br.com.fiap.oficina.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceOrderTest {

    @Test
     void deveCalcularOValorTotalCorretamente() {
        Service service1 = new Service("Serviço A", "Desc A", new BigDecimal("100.50"));

        Part part1 = new Part("Peça A", "Desc Peça A", new BigDecimal("50.25"), 10);
        Part part2 = new Part("Peça B", "Desc Peça B", new BigDecimal("20.00"), 5);

        OrderItem item1 = new OrderItem(part1, 2); // 2 x 50.25 = 100.50
        OrderItem item2 = new OrderItem(part2, 3); // 3 x 20.00 = 60.00

        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServices(List.of(service1));
        serviceOrder.setItems(List.of(item1, item2));

        serviceOrder.calculateTotalAmount();

        // Valor esperado: 100.50 (serviço) + 100.50 (item1) + 60.00 (item2) = 261.00
        BigDecimal expectedTotal = new BigDecimal("261.00");
        assertEquals(expectedTotal, serviceOrder.getTotalAmount());
    }
}