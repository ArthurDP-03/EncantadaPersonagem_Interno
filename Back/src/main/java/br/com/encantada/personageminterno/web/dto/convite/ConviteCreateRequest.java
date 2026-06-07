package br.com.encantada.personageminterno.web.dto.convite;

import io.swagger.v3.oas.annotations.media.Schema;
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
                description = "Lista de identificadores dos atores que receberão o convite",
                example = "[3, 7, 12]"
        )
        @NotEmpty
        List<Integer> atoresIds

) {
}