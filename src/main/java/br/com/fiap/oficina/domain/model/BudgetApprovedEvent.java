package br.com.fiap.oficina.domain.model;

import java.time.LocalDateTime;

public class BudgetApprovedEvent {
    private final Long serviceOrderId;
    private final LocalDateTime occurredAt;

    public BudgetApprovedEvent(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}




