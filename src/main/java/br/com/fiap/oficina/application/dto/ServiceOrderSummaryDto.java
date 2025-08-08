package br.com.fiap.oficina.application.dto;

import br.com.fiap.oficina.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrderSummaryDto {
    private Long id;
    private String clientName;
    private String vehiclePlate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime creationDate;

    public ServiceOrderSummaryDto(Long id, String clientName, String vehiclePlate, OrderStatus status, BigDecimal totalAmount, LocalDateTime creationDate) {
        this.id = id;
        this.clientName = clientName;
        this.vehiclePlate = vehiclePlate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}