package br.com.encantada.personageminterno.web.dto.eventopersonagem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para adicionar um personagem a um evento já criado")
public record AdicionarPersonagemRequest(

        @Schema(
                description = "Identificador do evento ao qual o personagem será vinculado",
                example = "1"
        )
        @NotNull
        Integer eventoId,

        @Schema(
                description = "Identificador do item de personagem (figurino) a ser vinculado ao evento",
                example = "5"
        )
        @NotNull
        Integer personagemItemId

) {
}
