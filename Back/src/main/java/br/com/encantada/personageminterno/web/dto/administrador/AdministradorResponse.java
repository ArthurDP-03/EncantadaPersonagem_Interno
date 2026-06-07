package br.com.encantada.personageminterno.web.dto.administrador;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno de um administrador cadastrado no sistema")
public record AdministradorResponse(

        @Schema(
                description = "Identificador único do administrador",
                example = "1"
        )
        Integer id,

        @Schema(
                description = "Nome completo do administrador",
                example = "Maria Silva"
        )
        String nome,

        @Schema(
                description = "Endereço de e-mail do administrador",
                example = "maria.silva@empresa.com.br"
        )
        String email,

        @Schema(
                description = "Telefone para contato do administrador",
                example = "(11) 98765-4321"
        )
        String telefone

) {
}