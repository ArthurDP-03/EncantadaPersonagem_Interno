package br.com.encantada.personageminterno.web.dto.personagemitem;

import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para atualização do status de um item de personagem")
public record PersonagemItemStatusRequest(

        @Schema(description = "Novo status do item", example = "MANUTENCAO")
        @NotNull
        PersonagemItemStatus status

) {
}
