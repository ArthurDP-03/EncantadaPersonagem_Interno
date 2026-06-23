package br.com.encantada.personageminterno.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.encantada.personageminterno.service.ConviteService;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.convite.ConviteRespostaRequest;
import jakarta.validation.Valid;

@Tag(name = "Convites", description = "Gerenciamento de convites para atores participarem de eventos")
@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }

    @Operation(summary = "Enviar convites", description = "Envia convites para atores participarem de um evento-personagem. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Convites enviados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento-personagem ou atores não encontrados")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> enviarConvites(
            @Valid @RequestBody ConviteCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conviteService.enviarConvites(request, authentication.getName()));
    }

    @Operation(summary = "Listar convites por evento-personagem", description = "Retorna todos os convites relacionados a um evento-personagem. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento-personagem não encontrado")
    })
    @GetMapping("/evento-personagem/{epId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> listarPorEventoPersonagem(@PathVariable Integer epId) {
        return ResponseEntity.ok(conviteService.listarPorEventoPersonagem(epId));
    }

    @Operation(summary = "Listar convites enviados pelo admin", description = "Retorna todos os convites enviados pelo administrador autenticado. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN")
    })
    @GetMapping("/enviadosADM")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> listarEnviados(Authentication authentication) {
        return ResponseEntity.ok(conviteService.listarMeusConvites(authentication.getName()));
    }

    @Operation(summary = "Listar convites do ator", description = "Retorna todos os convites recebidos pelo ator autenticado. Restrito a atores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ATOR")
    })
    @GetMapping("/conviteAtor")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<List<ConviteResponse>> listarMeus(
            @RequestParam(required = false) ConviteStatus status,
            Authentication authentication) {
        return ResponseEntity.ok(conviteService.listarMeus(authentication.getName(), status));
    }

    @Operation(summary = "Responder convite", description = "Ator aceita ou recusa um convite. Restrito a atores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status de resposta inválido"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ATOR"),
            @ApiResponse(responseCode = "404", description = "Convite não encontrado")
    })
    @PatchMapping("/{id}/responder")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<ConviteResponse> responder(
            @PathVariable Integer id,
            @Valid @RequestBody ConviteRespostaRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(conviteService.responder(id, request, authentication.getName()));
    }

    @Operation(summary = "Cancelar convite", description = "Cancela um convite enviado a um ator. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Convite cancelado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Convite não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id, Authentication authentication) {
        conviteService.cancelar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reativar convite", description = "Reativa um convite cancelado, retornando-o para PENDENTE. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convite reativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Convite não pode ser reativado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Convite não encontrado")
    })
    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConviteResponse> reativar(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(conviteService.reativar(id, authentication.getName()));
    }
}
