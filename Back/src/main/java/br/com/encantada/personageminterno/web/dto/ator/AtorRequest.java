package br.com.encantada.personageminterno.web.dto.ator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AtorRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String telefone,
        @Size(max = 20) String genero,
        BigDecimal altura,
        BigDecimal peso,
        @Size(max = 255) String observacao,
        Boolean ativo
) {
}
