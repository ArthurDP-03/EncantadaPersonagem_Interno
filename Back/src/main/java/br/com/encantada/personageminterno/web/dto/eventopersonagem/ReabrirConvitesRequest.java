package br.com.encantada.personageminterno.web.dto.eventopersonagem;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ReabrirConvitesRequest(
        @NotEmpty List<Integer> atoresIds) {
}
