package br.com.fiap.oficina.application.dto;

import br.com.fiap.oficina.domain.validation.CPFOrCNPJ;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientRequestDto {

    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
    private String name;

    @NotBlank(message = "Documento não pode ser vazio")
    @CPFOrCNPJ
    private String document;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }
}