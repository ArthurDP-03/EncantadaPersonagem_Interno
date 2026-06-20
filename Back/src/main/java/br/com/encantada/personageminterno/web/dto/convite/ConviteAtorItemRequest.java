package br.com.encantada.personageminterno.web.dto.convite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Par de ator e item de personagem para envio de convite")
public record ConviteAtorItemRequest(

        @Schema(description = "Identificador do ator convidado", example = "3")
        @NotNull
        Integer atorId,

        @Schema(description = "Identificador do item de personagem que será usado (ex: ALICE-001)", example = "1")
        @NotNull
        Integer personagemItemId

) {
}
