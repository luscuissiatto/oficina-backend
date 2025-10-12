package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.AverageServiceTimeDto;
import br.com.fiap.oficina.domain.model.OrderStatus;
import br.com.fiap.oficina.domain.model.ServiceOrder;
import br.com.fiap.oficina.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    public void deveCalcularTempoMedioCorretamente() {
        var oilChange = new br.com.fiap.oficina.domain.model.Service("Troca de Óleo", "Desc", BigDecimal.TEN);
        var brakes = new br.com.fiap.oficina.domain.model.Service("Revisão de Freios", "Desc", BigDecimal.TEN);

        ServiceOrder order1 = new ServiceOrder();
        order1.setStatus(OrderStatus.COMPLETED);
        order1.setCreationDate(LocalDateTime.now().minusHours(10));
        order1.setFinishDate(LocalDateTime.now().minusHours(8)); // 2h de duração
        order1.setServices(List.of(oilChange)); // Associa o serviço

        ServiceOrder order2 = new ServiceOrder();
        order2.setStatus(OrderStatus.DELIVERED);
        order2.setCreationDate(LocalDateTime.now().minusHours(6));
        order2.setFinishDate(LocalDateTime.now().minusHours(2)); // 4h de duração
        order2.setServices(List.of(oilChange)); // Associa o mesmo serviço

        ServiceOrder order3 = new ServiceOrder();
        order3.setStatus(OrderStatus.COMPLETED);
        order3.setCreationDate(LocalDateTime.now().minusHours(10));
        order3.setFinishDate(LocalDateTime.now().minusHours(5)); // 5h de duração
        order3.setServices(List.of(brakes)); // Associa o outro serviço

        when(serviceOrderRepository.findAllByStatusIn(anyList())).thenReturn(List.of(order1, order2, order3));

        List<AverageServiceTimeDto> result = analyticsService.getAverageServiceTime();

        assertEquals(2, result.size(), "Deveria haver 2 grupos de serviços (freios e óleo)");

        AverageServiceTimeDto brakesAvg = result.get(0);
        assertEquals("Revisão de Freios", brakesAvg.getServiceName());
        assertEquals(5.0, brakesAvg.getAverageTimeInHours(), 0.01, "A média para freios deveria ser 5.0 horas");

        AverageServiceTimeDto oilChangeAvg = result.get(1);
        assertEquals("Troca de Óleo", oilChangeAvg.getServiceName());
        assertEquals(3.0, oilChangeAvg.getAverageTimeInHours(), 0.01, "A média para troca de óleo (2h + 4h)/2 deveria ser 3.0 horas");
    }
}