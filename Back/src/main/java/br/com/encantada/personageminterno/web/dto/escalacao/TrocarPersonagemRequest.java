package br.com.encantada.personageminterno.web.dto.escalacao;

import jakarta.validation.constraints.NotNull;

public record TrocarPersonagemRequest(
    @NotNull Integer novoPersonagemId
) {}