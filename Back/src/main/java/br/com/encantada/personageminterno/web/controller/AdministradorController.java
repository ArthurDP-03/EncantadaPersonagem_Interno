package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.AdministradorService;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@Tag(name = "Administradores", description = "Gerenciamento de administradores da plataforma")
@RestController
@RequestMapping("/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @Operation(summary = "Listar administradores", description = "Retorna a lista de todos os administradores cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<AdministradorResponse>> listar() {
        return ResponseEntity.ok(administradorService.listar());
    }
    
    @Operation(summary = "Buscar administrador por ID", description = "Retorna os dados de um administrador específico pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(administradorService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar administrador", description = "Atualiza os dados de um administrador existente. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AdministradorRequest request,
            Authentication authentication) {
        String administradorEmail = authentication.getName();
        return ResponseEntity.ok(administradorService.atualizar(id, request, administradorEmail));
    }

    @Operation(summary = "Remover administrador", description = "Remove um administrador pelo ID. Restrito a administradores ou ao próprio usuário.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Administrador removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ATOR') and @securityService.isOwner(#id, authentication))")
    public ResponseEntity<Void> deletar(@PathVariable Integer id, Authentication authentication) {
        String administradorEmail = authentication.getName();
        administradorService.deletar(id, administradorEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Criar administrador", description = "Cadastra um novo administrador na plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @PostMapping
    public ResponseEntity<AdministradorResponse> criar(@Valid @RequestBody AdministradorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.criar(request));
    }
}
