package br.com.encantada.personageminterno.web.dto.convite;

import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados necessários para responder a um convite recebido por um ator")
public record ConviteRespostaRequest(

        @Schema(
                description = "Resposta do ator ao convite. Deve ser ACEITO ou RECUSADO",
                example = "ACEITO"
        )
        @NotNull
        ConviteStatus status

) {
}