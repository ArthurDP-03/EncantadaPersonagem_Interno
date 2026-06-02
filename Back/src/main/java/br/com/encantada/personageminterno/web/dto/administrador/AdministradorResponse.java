package br.com.encantada.personageminterno.web.dto.administrador;

public record AdministradorResponse(
        Integer id,
        String nome,
        String email,
        String telefone
) {
}
