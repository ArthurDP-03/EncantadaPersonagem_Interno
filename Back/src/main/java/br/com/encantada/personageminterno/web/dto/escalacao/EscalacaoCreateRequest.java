package br.com.encantada.personageminterno.web.dto.escalacao;

import jakarta.validation.constraints.NotNull;

public record EscalacaoCreateRequest(
    @NotNull Integer eventoPersonagemId,
    @NotNull Integer atorId,                 // deve ter convite ACEITO
    @NotNull Integer personagemItemId        // figurino reservado
) {}
