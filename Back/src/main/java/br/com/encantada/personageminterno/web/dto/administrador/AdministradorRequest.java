package br.com.encantada.personageminterno.web.dto.administrador;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastro ou atualização de um administrador do sistema")
public record AdministradorRequest(

        @Schema(
                description = "Nome completo do administrador",
                example = "Ana Costa"
        )
        @NotBlank @Size(max = 150)
        String nome,

        @Schema(
                description = "Endereço de e-mail utilizado para acesso ao sistema",
                example = "admin@encantada.com.br"
        )
        @NotBlank @Email @Size(max = 150)
        String email,

        @Schema(
                description = "Senha de acesso do administrador com no mínimo 8 caracteres",
                example = "P@ssw0rd"
        )
        @NotBlank @Size(min = 8, max = 255)
        String senha,

        @Schema(
                description = "Telefone para contato do administrador",
                example = "(41) 99999-9999"
        )
        @Size(max = 20)
        String telefone

) {
}