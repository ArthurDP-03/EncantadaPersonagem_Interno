package br.com.encantada.personageminterno.web.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta de um cliente")
public record ClienteResponse(
        @Schema(description = "Identificador único do cliente", example = "7")
        Integer id,

        @Schema(description = "Nome completo do cliente", example = "Maria Souza")
        String nome,

        @Schema(description = "Telefone de contato", example = "(41) 99999-1234")
        String telefone,

        @Schema(description = "E-mail do cliente", example = "maria.souza@email.com")
        String email
) {
}