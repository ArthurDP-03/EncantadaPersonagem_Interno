package br.com.encantada.personageminterno.web.dto.evento;

import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados necessários para cadastro ou atualização de um evento")
public record EventoRequest(

        @Schema(
                description = "Título do evento",
                example = "Festa Encantada da Alice"
        )
        @NotBlank @Size(max = 150)
        String titulo,

        @Schema(
                description = "Descrição detalhada do evento",
                example = "Festa infantil com apresentação de personagens e atividades recreativas."
        )
        @Size(max = 255)
        String descricao,

        @Schema(
                description = "Data e hora de início do evento",
                example = "2026-07-15T14:00:00"
        )
        @NotNull
        LocalDateTime dataInicio,

        @Schema(
                description = "Data e hora de término do evento",
                example = "2026-07-15T18:00:00"
        )
        @NotNull
        LocalDateTime dataFim,

        @Schema(
                description = "Endereço onde o evento será realizado",
                example = "Rua das Flores, 123 - Centro, Curitiba/PR"
        )
        @Size(max = 255)
        String endereco,

        @Schema(
                description = "Status atual do evento",
                example = "AGENDADO"
        )
        EventoStatus status,

        @Schema(
                description = "Forma de pagamento acordada para o evento",
                example = "PIX"
        )
        @Size(max = 50)
        String tipoPagamento,

        @Schema(
                description = "Valor total contratado para o evento",
                example = "850.00"
        )
        BigDecimal valorTotal,

        @Schema(
                description = "Identificador do cliente responsável pela contratação do evento",
                example = "5"
        )
        @NotNull
        Integer clienteId

) {
}