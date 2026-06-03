package br.com.encantada.personageminterno.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.encantada.personageminterno.service.EventoPersonagemService;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.escalacao.TrocarPersonagemRequest;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.ReabrirConvitesRequest;
import jakarta.validation.Valid;

@Tag(name = "Evento-Personagens", description = "Gerenciamento de personagens em eventos")
@RestController
@RequestMapping("/evento-personagens")
public class EventoPersonagemController {

    private final EventoPersonagemService eventoPersonagemService;

    public EventoPersonagemController(EventoPersonagemService eventoPersonagemService) {
        this.eventoPersonagemService = eventoPersonagemService;
    }

    @Operation(summary = "Trocar personagem", description = "Substitui o personagem de um evento-personagem. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personagem trocado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento-personagem ou personagem não encontrado")
    })
    @PatchMapping("/{id}/trocar-personagem")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoPersonagemResponse> trocarPersonagem(
            @PathVariable Integer id,
            @Valid @RequestBody TrocarPersonagemRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                eventoPersonagemService.trocarPersonagem(id, request, authentication.getName()));
    }

    @Operation(summary = "Reabrir convites", description = "Reabre convites para novos atores de um evento-personagem. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Convites reabertozados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento-personagem ou atores não encontrados")
    })
    @PostMapping("/{id}/reabrir-convites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> reabrirConvites(
            @PathVariable Integer id,
            @Valid @RequestBody ReabrirConvitesRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventoPersonagemService.reabrirConvites(id, request, authentication.getName()));
    }
}
