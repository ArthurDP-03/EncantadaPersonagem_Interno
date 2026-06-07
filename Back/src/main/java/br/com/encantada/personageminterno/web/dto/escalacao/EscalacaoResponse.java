package br.com.encantada.personageminterno.web.dto.escalacao;

import br.com.encantada.personageminterno.domain.enums.EscalacaoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados completos de uma escalação")
public record EscalacaoResponse(
        @Schema(description = "Identificador único da escalação", example = "1")
        Integer id,

        @Schema(description = "ID do vínculo entre evento e personagem", example = "3")
        Integer eventoPersonagemId,

        @Schema(description = "ID do evento", example = "5")
        Integer eventoId,

        @Schema(description = "Título do evento", example = "Festa Encantada — Dezembro 2025")
        String eventoTitulo,

        @Schema(description = "ID do personagem", example = "2")
        Integer personagemId,

        @Schema(description = "Nome do personagem", example = "Cinderela")
        String personagemNome,

        @Schema(description = "ID do ator escalado", example = "12")
        Integer atorId,

        @Schema(description = "Nome do ator escalado", example = "Ana Paula Ferreira")
        String atorNome,

        @Schema(description = "ID do item de figurino reservado", example = "7")
        Integer personagemItemId,

        @Schema(description = "Código do figurino reservado", example = "FIG-CIN-001")
        String personagemItemCodigo,

        @Schema(description = "ID do administrador que realizou a escalação", example = "1")
        Integer administradorId,

        @Schema(description = "Status atual da escalação", example = "CONFIRMADO")
        EscalacaoStatus status,

        @Schema(description = "Data e hora em que a escalação foi realizada", example = "2025-11-10T14:30:00")
        LocalDateTime dataEscolha,

        @Schema(description = "Data e hora em que o ator confirmou presença", example = "2025-11-11T09:00:00")
        LocalDateTime dataConfirmacaoAtor
) {}