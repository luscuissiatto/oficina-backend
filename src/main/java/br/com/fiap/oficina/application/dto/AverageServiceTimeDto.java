package br.com.fiap.oficina.application.dto;

public class AverageServiceTimeDto {
    private String serviceName;
    private double averageTimeInHours;

    public AverageServiceTimeDto(String serviceName, double averageTimeInHours) {
        this.serviceName = serviceName;
        this.averageTimeInHours = averageTimeInHours;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getAverageTimeInHours() {
        return averageTimeInHours;
    }

    public void setAverageTimeInHours(double averageTimeInHours) {
        this.averageTimeInHours = averageTimeInHours;
    }
}