package br.com.fiap.oficina.domain.model;

public enum OrderStatus {
    RECEIVED,           // Recebida
    IN_DIAGNOSIS,       // Em Diagnóstico
    AWAITING_APPROVAL,  // Aguardando Aprovação
    IN_PROGRESS,        // Em Execução
    COMPLETED,          // Finalizada
    DELIVERED;          // Entregue
}