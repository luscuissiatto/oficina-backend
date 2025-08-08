package br.com.fiap.oficina.domain.model;

public class Vehicle {
    private Long id;
    private String plate;
    private String brand;
    private String model;
    private Integer year;
    private Client client;

    public Vehicle() {
    }

    public Vehicle(String plate, String brand, String model, Integer year, Client client) {
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
