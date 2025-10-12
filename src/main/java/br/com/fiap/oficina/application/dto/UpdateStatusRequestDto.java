package br.com.fiap.oficina.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UpdateStatusRequestDto {
    @NotBlank(message = "O novo status não pode ser nulo")
    @Schema(description = "Novo status da Ordem de Serviço",
            example = "IN_PROGRESS",
            allowableValues = {"IN_DIAGNOSIS", "AWAITING_APPROVAL", "IN_PROGRESS", "COMPLETED", "DELIVERED"})
    private String newStatus;

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
