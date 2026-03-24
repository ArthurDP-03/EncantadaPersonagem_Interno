package br.com.encantada.personageminterno.web.dto.personagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonagemRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 255) String descricao,
        @Size(max = 255) String foto
) {
}
