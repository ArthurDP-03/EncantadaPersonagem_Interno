package br.com.encantada.personageminterno.web.dto.personagem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastro ou atualização de um personagem")
public record PersonagemRequest(

        @Schema(
                description = "Nome do personagem",
                example = "Alice"
        )
        @NotBlank @Size(max = 100)
        String nome,

        @Schema(
                description = "Descrição do personagem e suas características",
                example = "Personagem inspirada em Alice no País das Maravilhas, indicada para festas infantis e eventos temáticos."
        )
        @Size(max = 255)
        String descricao,

        @Schema(
                description = "URL da foto ou imagem de referência do personagem",
                example = "https://encantada.com.br/imagens/personagens/alice.jpg"
        )
        @Size(max = 255)
        String foto

) {
}