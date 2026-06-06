package br.com.encantada.personageminterno.web.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de um cliente")
public record ClienteRequest(
        @Schema(description = "Nome completo do cliente", example = "Maria Souza")
        @NotBlank @Size(max = 150) String nome,

        @Schema(description = "Telefone de contato", example = "(41) 99999-1234")
        @Size(max = 20) String telefone,

        @Schema(description = "E-mail do cliente", example = "maria.souza@email.com")
        @Size(max = 150) String email
) {
}