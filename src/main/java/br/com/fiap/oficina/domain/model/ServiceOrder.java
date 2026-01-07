package br.com.fiap.oficina.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrder {
    private Long id;
    private Client client;
    private Vehicle vehicle;
    private List<Service> services;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime updatedAt;
    private LocalDateTime finishDate;
    private BigDecimal totalAmount;
    private final List<Object> domainEvents = new ArrayList<>();

    public void calculateTotalAmount() {
        BigDecimal totalServices = this.services.stream()
                .map(Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalParts = this.items.stream()
                .map(item -> item.getPart().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = totalServices.add(totalParts);

        this.totalAmount = total.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BudgetApprovedEvent approveBudget() {
        if (this.status != OrderStatus.AWAITING_APPROVAL) {
            throw new IllegalStateException(
                String.format("Não é possível aprovar orçamento. Status atual: %s. Status esperado: AWAITING_APPROVAL", 
                    this.status)
            );
        }

        this.status = OrderStatus.IN_PROGRESS;
        BudgetApprovedEvent event = new BudgetApprovedEvent(this.id);
        this.domainEvents.add(event);
        return event;
    }

    public BudgetRefusedEvent refuseBudget() {
        if (this.status != OrderStatus.AWAITING_APPROVAL) {
            throw new IllegalStateException(
                String.format("Não é possível recusar orçamento. Status atual: %s. Status esperado: AWAITING_APPROVAL", 
                    this.status)
            );
        }

        this.status = OrderStatus.REFUSED;
        this.finishDate = LocalDateTime.now();
        BudgetRefusedEvent event = new BudgetRefusedEvent(this.id);
        this.domainEvents.add(event);
        return event;
    }

    public List<Object> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}