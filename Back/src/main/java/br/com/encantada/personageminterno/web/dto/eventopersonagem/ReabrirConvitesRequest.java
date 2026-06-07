package br.com.encantada.personageminterno.web.dto.eventopersonagem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Dados necessários para reabrir convites de atores para um personagem em um evento")
public record ReabrirConvitesRequest(

        @Schema(
                description = "Lista de identificadores dos atores que receberão novamente o convite",
                example = "[4, 8, 15]"
        )
        @NotEmpty
        List<Integer> atoresIds

) {
}