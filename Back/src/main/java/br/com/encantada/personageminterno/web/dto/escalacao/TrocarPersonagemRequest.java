package br.com.encantada.personageminterno.web.dto.escalacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para troca de personagem em uma escalação")
public record TrocarPersonagemRequest(
        @Schema(description = "ID do novo item de personagem (figurino) a ser atribuído", example = "4")
        @NotNull Integer novoPersonagemItemId
) {}