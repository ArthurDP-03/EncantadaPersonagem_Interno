package br.com.encantada.personageminterno.web.dto.cliente;

public record ClienteResponse(
        Integer id,
        String nome,
        String telefone,
        String email
) {
}
