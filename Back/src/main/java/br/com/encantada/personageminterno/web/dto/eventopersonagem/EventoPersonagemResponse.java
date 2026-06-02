package br.com.encantada.personageminterno.web.dto.eventopersonagem;

public record EventoPersonagemResponse(
        Integer id,
        Integer eventoId,
        String eventoTitulo,
        Integer personagemId,
        String personagemNome) {
}
