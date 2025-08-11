package br.com.fiap.oficina.domain.model;

import br.com.fiap.oficina.domain.validation.CPFOrCNPJ;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Client {
    private Long id;
    private String name;
    private String document;

    public Client() {
    }

    public Client(String name, String document) {
        this.name = name;
        this.document = document;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client cliente = (Client) o;
        return Objects.equals(id, cliente.id) && Objects.equals(name, cliente.name) && Objects.equals(document, cliente.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, document);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                '}';
    }
}
