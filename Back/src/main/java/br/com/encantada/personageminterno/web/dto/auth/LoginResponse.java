package br.com.encantada.personageminterno.web.dto.auth;

public record LoginResponse(
        String token,
        String nome,
        String email,
        String tipo
) {
}
