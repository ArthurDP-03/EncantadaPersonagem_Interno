package br.com.encantada.personageminterno.web.dto.convite;

import java.time.LocalDateTime;

import br.com.encantada.personageminterno.domain.enums.ConviteStatus;

public record ConviteResponse(
    Integer id,
    Integer eventoPersonagemId,
    Integer eventoId,
    String eventoTitulo,
    Integer personagemId,
    String personagemNome,
    Integer atorId,
    String atorNome,
    Integer administradorId,
    ConviteStatus status,
    LocalDateTime dataEnvio,
    LocalDateTime dataResposta
) {}
