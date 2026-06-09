package br.com.encantada.personageminterno.web.dto.personagemitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados necessários para cadastro de um item de personagem (estoque)")
public record PersonagemItemRequest(

        @Schema(description = "ID do personagem ao qual este item pertence", example = "1")
        @NotNull
        Integer idPersonagem

) {
}
