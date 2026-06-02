package br.com.encantada.personageminterno.web.dto.convite;

import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import jakarta.validation.constraints.NotNull;

public record ConviteRespostaRequest(
    @NotNull ConviteStatus status   // ACEITO ou RECUSADO
) {}
