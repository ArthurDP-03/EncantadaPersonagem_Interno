package br.com.encantada.personageminterno.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após a autenticação bem-sucedida de um usuário")
public record LoginResponse(

        @Schema(
                description = "Token JWT utilizado para autenticação nas demais requisições",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvQGVtcHJlc2EuY29tLmJyIiwiaWF0IjoxNzE4MDAwMDAwLCJleHAiOjE3MTgwODY0MDB9.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
        )
        String token,

        @Schema(
                description = "Nome do usuário autenticado",
                example = "Maria Silva"
        )
        String nome,

        @Schema(
                description = "Endereço de e-mail do usuário autenticado",
                example = "maria.silva@empresa.com.br"
        )
        String email,

        @Schema(
                description = "Tipo de perfil do usuário autenticado",
                example = "ADMIN"
        )
        String tipo

) {
}