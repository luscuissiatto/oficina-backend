package br.com.fiap.oficina.application.dto;

import java.util.List;

public class AddItemsToOrderRequestDto {
    private List<Long> serviceIds;
    private List<OrderItemRequestDto> items;

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }
}
