package br.com.encantada.personageminterno.web.dto.convite;

import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de retorno de um convite enviado a um ator para interpretar um personagem em um evento")
public record ConviteResponse(

        @Schema(
                description = "Identificador único do convite",
                example = "42"
        )
        Integer id,

        @Schema(
                description = "Identificador da associação entre evento e personagem",
                example = "15"
        )
        Integer eventoPersonagemId,

        @Schema(
                description = "Identificador do evento",
                example = "8"
        )
        Integer eventoId,

        @Schema(
                description = "Título do evento",
                example = "Festa Encantada da Alice"
        )
        String eventoTitulo,

        @Schema(
                description = "Identificador do personagem",
                example = "3"
        )
        Integer personagemId,

        @Schema(
                description = "Nome do personagem",
                example = "Alice"
        )
        String personagemNome,

        @Schema(
                description = "Identificador do item de personagem utilizado no convite",
                example = "1"
        )
        Integer personagemItemId,

        @Schema(
                description = "Código do item de personagem (ex: ALICE-001)",
                example = "ALICE-001"
        )
        String personagemItemCodigo,

        @Schema(
                description = "Identificador do ator convidado",
                example = "12"
        )
        Integer atorId,

        @Schema(
                description = "Nome do ator convidado",
                example = "Carlos Oliveira"
        )
        String atorNome,

        @Schema(
                description = "Identificador do administrador responsável pelo envio do convite",
                example = "1"
        )
        Integer administradorId,

        @Schema(
                description = "Status atual do convite",
                example = "PENDENTE"
        )
        ConviteStatus status,

        @Schema(
                description = "Data e hora em que o convite foi enviado",
                example = "2026-06-03T14:30:00"
        )
        LocalDateTime dataEnvio,

        @Schema(
                description = "Data e hora limite para resposta ao convite",
                example = "2026-06-05T23:59:59"
        )
        LocalDateTime dataExpiracao,

        @Schema(
                description = "Data e hora da resposta do ator ao convite",
                example = "2026-06-04T09:15:00"
        )
        LocalDateTime dataResposta

) {
}