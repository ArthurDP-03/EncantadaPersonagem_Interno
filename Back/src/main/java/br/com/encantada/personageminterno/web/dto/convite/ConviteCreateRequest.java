package br.com.encantada.personageminterno.web.dto.convite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dados necessários para criar convites de atores para um personagem em um evento")
public record ConviteCreateRequest(

        @Schema(
                description = "Identificador da associação entre evento e personagem para a qual os convites serão enviados",
                example = "15"
        )
        @NotNull
        Integer eventoPersonagemId,

        @Schema(
                description = "Lista de pares ator + item de personagem que receberão o convite"
        )
        @NotEmpty @Valid
        List<ConviteAtorItemRequest> convites

) {
}