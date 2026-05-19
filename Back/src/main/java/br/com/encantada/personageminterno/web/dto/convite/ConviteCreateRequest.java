package br.com.encantada.personageminterno.web.dto.convite;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ConviteCreateRequest(
    @NotNull Integer eventoPersonagemId,
    @NotEmpty List<Integer> atoresIds
) {}