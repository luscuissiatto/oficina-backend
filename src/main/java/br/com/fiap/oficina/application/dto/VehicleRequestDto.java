package br.com.fiap.oficina.application.dto;

import jakarta.validation.constraints.Pattern;

public class VehicleRequestDto {

    @Pattern(regexp = "[A-Z]{3}\\d[A-Z\\d]\\d{2}", message = "Formato de placa inválido")
    private String plate;
    private String brand;
    private String model;
    private Integer year;
    private Long clientId;

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
