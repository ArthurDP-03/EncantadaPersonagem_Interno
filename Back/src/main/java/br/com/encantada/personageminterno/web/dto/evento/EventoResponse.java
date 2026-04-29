package br.com.encantada.personageminterno.web.dto.evento;

import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoResponse(
        Integer id,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String endereco,
        EventoStatus status,
        String tipoPagamento,
        BigDecimal valorTotal,
        Integer clienteId,
        String clienteNome,
        Integer administradorCriadorId,
        String administradorCriadorNome
) {
}
