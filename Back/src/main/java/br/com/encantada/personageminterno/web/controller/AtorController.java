package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.AtorService;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Atores", description = "Gerenciamento de atores da plataforma")
@RestController
@RequestMapping("/atores")
public class AtorController {

    private final AtorService atorService;

    public AtorController(AtorService atorService) {
        this.atorService = atorService;
    }

    @Operation(summary = "Listar atores", description = "Retorna a lista de todos os atores cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<AtorResponse>> listar() {
        return ResponseEntity.ok(atorService.listar());
    }

    @Operation(summary = "Buscar ator por ID", description = "Retorna os dados de um ator específico pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ator encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Ator não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AtorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(atorService.buscarPorId(id));
    }

    @Operation(summary = "Remover ator", description = "Remove um ator pelo ID. Restrito a administradores ou ao próprio ator.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ator removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Ator não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAtorOwner(#id, authentication)")
    public ResponseEntity<Void> deletar(@PathVariable Integer id, Authentication authentication) {
        atorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar ator", description = "Atualiza os dados de um ator existente. Restrito a administradores ou ao próprio ator.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ator atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Ator não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAtorOwner(#id, authentication)")
    public ResponseEntity<AtorResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AtorRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(atorService.atualizar(id, request));
    }

    @Operation(summary = "Criar ator", description = "Cadastra um novo ator na plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ator criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @PostMapping
    public ResponseEntity<AtorResponse> criar(@Valid @RequestBody AtorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atorService.criar(request));
    }
}
