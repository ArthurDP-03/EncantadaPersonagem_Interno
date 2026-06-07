package br.com.encantada.personageminterno.web.dto.ator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados necessários para cadastro ou atualização de um ator")
public record AtorRequest(

        @Schema(
                description = "Nome completo do ator",
                example = "Carlos Oliveira"
        )
        @NotBlank @Size(max = 150)
        String nome,

        @Schema(
                description = "Endereço de e-mail do ator",
                example = "carlos.oliveira@email.com"
        )
        @NotBlank @Email @Size(max = 150)
        String email,

        @Schema(
                description = "Senha de acesso do ator com no mínimo 8 caracteres",
                example = "SenhaForte@123"
        )
        @NotBlank @Size(min = 8, max = 255)
        String senha,

        @Schema(
                description = "Telefone para contato do ator",
                example = "(11) 98765-4321"
        )
        @Size(max = 20)
        String telefone,

        @Schema(
                description = "Gênero do ator",
                example = "Masculino"
        )
        @Size(max = 20)
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
        @Size(max = 255)
        String observacao,

        @Schema(
                description = "Indica se o ator está ativo no sistema",
                example = "true"
        )
        Boolean ativo

) {
}