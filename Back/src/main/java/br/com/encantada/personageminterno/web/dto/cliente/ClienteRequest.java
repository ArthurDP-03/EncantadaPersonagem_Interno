package br.com.encantada.personageminterno.web.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 20) String telefone,
        @Size(max = 150) String email
) {
}
