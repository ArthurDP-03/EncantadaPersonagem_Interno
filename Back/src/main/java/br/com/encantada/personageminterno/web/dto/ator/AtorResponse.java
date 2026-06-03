package br.com.encantada.personageminterno.web.dto.ator;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados de retorno de um ator cadastrado no sistema")
public record AtorResponse(

        @Schema(
                description = "Identificador único do ator",
                example = "1"
        )
        Integer id,

        @Schema(
                description = "Nome completo do ator",
                example = "Carlos Oliveira"
        )
        String nome,

        @Schema(
                description = "Endereço de e-mail do ator",
                example = "carlos.oliveira@email.com"
        )
        String email,

        @Schema(
                description = "Telefone para contato do ator",
                example = "(11) 98765-4321"
        )
        String telefone,

        @Schema(
                description = "Gênero do ator",
                example = "Masculino"
        )
        String genero,

        @Schema(
                description = "Altura do ator em metros",
                example = "1.78"
        )
        BigDecimal altura,

        @Schema(
                description = "Peso do ator em quilogramas",
                example = "75.5"
        )
        BigDecimal peso,

        @Schema(
                description = "Observações adicionais sobre o ator",
                example = "Experiência em apresentações infantis e eventos corporativos."
        )
        String observacao,

        @Schema(
                description = "Indica se o ator está ativo no sistema",
                example = "true"
        )
        Boolean ativo

) {
}