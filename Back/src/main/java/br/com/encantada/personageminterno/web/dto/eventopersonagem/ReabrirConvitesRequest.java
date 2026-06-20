package br.com.encantada.personageminterno.web.dto.eventopersonagem;

import br.com.encantada.personageminterno.web.dto.convite.ConviteAtorItemRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Dados necessários para reabrir convites de atores para um personagem em um evento")
public record ReabrirConvitesRequest(

        @Schema(
                description = "Lista de pares ator + item de personagem que receberão novamente o convite"
        )
        @NotEmpty @Valid
        List<ConviteAtorItemRequest> convites

) {
}