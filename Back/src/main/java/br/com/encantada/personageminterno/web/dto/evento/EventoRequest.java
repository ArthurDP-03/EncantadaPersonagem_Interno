package br.com.encantada.personageminterno.web.dto.evento;

import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoRequest(
        @NotBlank @Size(max = 150) String titulo,
        @Size(max = 255) String descricao,
        @NotNull LocalDateTime dataInicio,
        @NotNull LocalDateTime dataFim,
        @Size(max = 255) String endereco,
        EventoStatus status,
        @Size(max = 50) String tipoPagamento,
        BigDecimal valorTotal,
        @NotNull Integer clienteId
) {
}
