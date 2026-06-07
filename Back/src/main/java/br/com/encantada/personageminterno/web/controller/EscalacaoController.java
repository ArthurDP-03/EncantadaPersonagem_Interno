package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.EscalacaoService;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoCreateRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Escalações", description = "Gerenciamento de escalações de atores para eventos")
@RestController
@RequestMapping("/escalacoes")
public class EscalacaoController {

    private final EscalacaoService escalacaoService;

    public EscalacaoController(EscalacaoService escalacaoService) {
        this.escalacaoService = escalacaoService;
    }

    @Operation(summary = "Escalar ator para evento", description = "Escolhe um ator final para um personagem em um evento. O ator deve ter convite aceito e figurino reservado. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ator escalado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou ator sem convite aceito ou figurino não reservado"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
        @ApiResponse(responseCode = "404", description = "EventoPersonagem, ator ou figurino não encontrado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EscalacaoResponse> escolherAtorFinal(
            @Valid @RequestBody EscalacaoCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(escalacaoService.escolherAtorFinal(request, authentication.getName()));
    }

    @Operation(summary = "Confirmar presença", description = "Permite que o ator escalado confirme sua presença no evento.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Presença confirmada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ATOR ou o ator não pertence a esta escalação"),
        @ApiResponse(responseCode = "404", description = "Escalação não encontrada")
    })
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<EscalacaoResponse> confirmarPresenca(
            @PathVariable Integer id,
            Authentication authentication) {
        return ResponseEntity.ok(escalacaoService.confirmarPresenca(id, authentication.getName()));
    }

    @Operation(summary = "Cancelar escalação", description = "Cancela a escalação de um ator de um evento. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Escalação cancelada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
        @ApiResponse(responseCode = "404", description = "Escalação não encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id, Authentication authentication) {
        escalacaoService.cancelar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar escalação por ID", description = "Retorna os detalhes completos de uma escalação pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Escalação encontrada"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
        @ApiResponse(responseCode = "404", description = "Escalação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EscalacaoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(escalacaoService.buscarPorId(id));
    }
}