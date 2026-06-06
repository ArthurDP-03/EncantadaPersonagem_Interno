package br.com.encantada.personageminterno.web.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "Dados de resposta do dashboard administrativo")
public record DashboardResponse(

        @Schema(description = "Total de eventos cadastrados", example = "12")
        Long totalEventos,

        @Schema(description = "Quantidade de eventos agrupados por status",
                example = "{\"CONFIRMADO\": 8, \"RASCUNHO\": 3, \"CANCELADO\": 1}")
        Map<String, Long> eventosPorStatus,

        @Schema(description = "Soma do valor total dos eventos confirmados", example = "45000.00")
        BigDecimal faturamentoTotal,

        @Schema(description = "Total de atores com cadastro ativo", example = "10")
        Long totalAtoresAtivos,

        @Schema(description = "Total de convites com status PENDENTE", example = "3")
        Long convitesPendentes,

        @Schema(description = "Total de escalações aguardando confirmação do ator", example = "2")
        Long escalacoesPendentes,

        @Schema(description = "Lista dos próximos eventos futuros")
        List<ProximoEventoResponse> proximosEventos,

        @Schema(description = "Quantidade de eventos e faturamento agrupados por mês")
        List<EventoMesResponse> eventosPorMes,

        @Schema(description = "Personagens com maior número de escalações")
        List<PersonagemEscaladoResponse> personagensMaisEscalados,

        @Schema(description = "Alertas operacionais que requerem atenção")
        AlertasResponse alertas
) {

    @Schema(description = "Dados de um próximo evento")
    public record ProximoEventoResponse(

            @Schema(description = "Título do evento", example = "Festa Infantil Temática")
            String titulo,

            @Schema(description = "Nome do cliente responsável", example = "Buffet Premium Eventos")
            String cliente,

            @Schema(description = "Data e hora de início", example = "2026-07-15T14:00:00")
            LocalDateTime dataInicio,

            @Schema(description = "Status atual do evento", example = "CONFIRMADO")
            String status,

            @Schema(description = "Valor total contratado", example = "2500.00")
            BigDecimal valorTotal
    ) {}

    @Schema(description = "Eventos e faturamento agrupados por mês")
    public record EventoMesResponse(

            @Schema(description = "Mês de referência no formato YYYY-MM", example = "2026-06")
            String mes,

            @Schema(description = "Quantidade de eventos no mês", example = "4")
            Long quantidade,

            @Schema(description = "Soma do faturamento no mês", example = "12000.00")
            BigDecimal faturamento
    ) {}

    @Schema(description = "Personagem e total de escalações realizadas")
    public record PersonagemEscaladoResponse(

            @Schema(description = "Nome do personagem", example = "Princesa Encantada")
            String personagem,

            @Schema(description = "Total de escalações confirmadas", example = "7")
            Long totalEscalacoes
    ) {}

    @Schema(description = "Alertas operacionais do sistema")
    public record AlertasResponse(

            @Schema(description = "Eventos confirmados sem escalação definida", example = "2")
            Long eventosSemEscalacao,

            @Schema(description = "Escalações aguardando confirmação do ator", example = "1")
            Long escalacoesPendentes7Dias,

            @Schema(description = "Itens de fantasia em manutenção", example = "1")
            Long itensManutencao
    ) {}
}