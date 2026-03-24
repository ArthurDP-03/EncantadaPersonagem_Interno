package br.com.encantada.personageminterno.web.dto.ator;

import java.math.BigDecimal;

public record AtorResponse(
        Integer id,
        String nome,
        String email,
        String telefone,
        String genero,
        BigDecimal altura,
        BigDecimal peso,
        String observacao,
        Boolean ativo
) {
}
