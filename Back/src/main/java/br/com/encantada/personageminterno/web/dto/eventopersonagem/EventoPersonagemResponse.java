package br.com.encantada.personageminterno.web.dto.eventopersonagem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno da associação entre um evento e um personagem")
public record EventoPersonagemResponse(

        @Schema(
                description = "Identificador único da associação entre evento e personagem",
                example = "12"
        )
        Integer id,

        @Schema(
                description = "Identificador do evento",
                example = "10"
        )
        Integer eventoId,

        @Schema(
                description = "Título do evento",
                example = "Festa Encantada da Alice"
        )
        String eventoTitulo,

        @Schema(
                description = "Identificador do personagem",
                example = "3"
        )
        Integer personagemId,

        @Schema(
                description = "Nome do personagem associado ao evento",
                example = "Alice"
        )
        String personagemNome,

        @Schema(
                description = "Quantidade de itens com status DISPONIVEL para este personagem",
                example = "3"
        )
        long estoqueDisponivel

) {
}