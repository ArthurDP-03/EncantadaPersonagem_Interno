package br.com.encantada.personageminterno.web.dto.personagem;

public record PersonagemResponse(
        Integer id,
        String nome,
        String descricao,
        String foto
) {
}
