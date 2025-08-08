package br.com.fiap.oficina.domain.model;

public class OrderItem {
    private Long id;
    private Part part;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(Part part, int quantity) {
        this.part = part;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}