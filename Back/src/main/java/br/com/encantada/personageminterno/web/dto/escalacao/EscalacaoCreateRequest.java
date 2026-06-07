package br.com.encantada.personageminterno.web.dto.escalacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para escalar um ator em um evento")
public record EscalacaoCreateRequest(
        @Schema(description = "ID do vínculo entre evento e personagem", example = "3")
        @NotNull Integer eventoPersonagemId,

        @Schema(description = "ID do ator a ser escalado — deve possuir convite aceito", example = "12")
        @NotNull Integer atorId,

        @Schema(description = "ID do item de figurino reservado para o personagem", example = "7")
        @NotNull Integer personagemItemId
) {}