package br.com.encantada.personageminterno.web.dto.personagem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um personagem cadastrado no sistema")
public record PersonagemResponse(

        @Schema(
                description = "Identificador único do personagem",
                example = "3"
        )
        Integer id,

        @Schema(
                description = "Nome do personagem",
                example = "Alice"
        )
        String nome,

        @Schema(
                description = "Descrição do personagem e suas características",
                example = "Personagem inspirada em Alice no País das Maravilhas, indicada para festas infantis e eventos temáticos."
        )
        String descricao,

        @Schema(
                description = "URL da foto ou imagem de referência do personagem",
                example = "https://encantada.com.br/imagens/personagens/alice.jpg"
        )
        String foto

) {
}