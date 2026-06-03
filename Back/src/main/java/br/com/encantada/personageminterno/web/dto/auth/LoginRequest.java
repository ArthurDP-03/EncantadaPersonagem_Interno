package br.com.encantada.personageminterno.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para autenticação de um usuário no sistema")
public record LoginRequest(

        @Schema(
                description = "Endereço de e-mail utilizado para login",
                example = "usuario@empresa.com.br"
        )
        @NotBlank @Email
        String email,

        @Schema(
                description = "Senha de acesso do usuário",
                example = "SenhaSegura@123"
        )
        @NotBlank
        String senha

) {
}