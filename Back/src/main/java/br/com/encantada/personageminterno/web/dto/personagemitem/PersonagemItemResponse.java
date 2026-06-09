package br.com.encantada.personageminterno.web.dto.personagemitem;

import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um item de personagem")
public record PersonagemItemResponse(

        @Schema(description = "Identificador único do item", example = "5")
        Integer id,

        @Schema(description = "ID do personagem ao qual o item pertence", example = "1")
        Integer idPersonagem,

        @Schema(description = "Nome do personagem ao qual o item pertence", example = "Alice")
        String nomePersonagem,

        @Schema(description = "Código único que identifica o item físico", example = "ALICE-001")
        String codigo,

        @Schema(description = "Status atual do item", example = "DISPONIVEL")
        PersonagemItemStatus status

) {
}
