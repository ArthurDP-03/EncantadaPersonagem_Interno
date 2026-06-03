package br.com.encantada.personageminterno.web.dto.evento;

import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de retorno de um evento cadastrado no sistema")
public record EventoResponse(

        @Schema(
                description = "Identificador único do evento",
                example = "10"
        )
        Integer id,

        @Schema(
                description = "Título do evento",
                example = "Festa Encantada da Alice"
        )
        String titulo,

        @Schema(
                description = "Descrição detalhada do evento",
                example = "Festa infantil com apresentação de personagens e atividades recreativas."
        )
        String descricao,

        @Schema(
                description = "Data e hora de início do evento",
                example = "2026-07-15T14:00:00"
        )
        LocalDateTime dataInicio,

        @Schema(
                description = "Data e hora de término do evento",
                example = "2026-07-15T18:00:00"
        )
        LocalDateTime dataFim,

        @Schema(
                description = "Endereço onde o evento será realizado",
                example = "Rua das Flores, 123 - Centro, Curitiba/PR"
        )
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
        String tipoPagamento,

        @Schema(
                description = "Valor total contratado para o evento",
                example = "850.00"
        )
        BigDecimal valorTotal,

        @Schema(
                description = "Identificador do cliente responsável pelo evento",
                example = "5"
        )
        Integer clienteId,

        @Schema(
                description = "Nome do cliente responsável pelo evento",
                example = "Ana Souza"
        )
        String clienteNome,

        @Schema(
                description = "Identificador do administrador que criou o evento",
                example = "1"
        )
        Integer administradorCriadorId,

        @Schema(
                description = "Nome do administrador que criou o evento",
                example = "Mariana Costa"
        )
        String administradorCriadorNome

) {
}