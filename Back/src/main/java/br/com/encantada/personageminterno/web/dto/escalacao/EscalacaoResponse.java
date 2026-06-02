package br.com.encantada.personageminterno.web.dto.escalacao;

import java.time.LocalDateTime;

import br.com.encantada.personageminterno.domain.enums.EscalacaoStatus;

public record EscalacaoResponse(
        Integer id,
        Integer eventoPersonagemId,
        Integer eventoId,
        String eventoTitulo,
        Integer personagemId,
        String personagemNome,
        Integer atorId,
        String atorNome,
        Integer personagemItemId,
        String personagemItemCodigo,
        Integer administradorId,
        EscalacaoStatus status,
        LocalDateTime dataEscolha,
        LocalDateTime dataConfirmacaoAtor) {
}
