package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.PersonagemService;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

@Tag(name = "Personagens", description = "Gerenciamento de personagens da plataforma")
@RestController
@RequestMapping("/personagens")
public class PersonagemController {

    private final PersonagemService personagemService;

    public PersonagemController(PersonagemService personagemService) {
        this.personagemService = personagemService;
    }

    @Operation(summary = "Listar personagens", description = "Retorna a lista de todos os personagens cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<PersonagemResponse>> listar() {
        return ResponseEntity.ok(personagemService.listar());
    }

    @Operation(summary = "Buscar personagem por ID", description = "Retorna os dados de um personagem específico pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personagem encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Personagem não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonagemResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(personagemService.buscarPorId(id));
    }

    @Operation(summary = "Remover personagem", description = "Remove um personagem pelo ID. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personagem removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Personagem não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        personagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar personagem", description = "Atualiza os dados de um personagem existente. Restrito a administradores.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personagem atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Personagem não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonagemResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.ok(personagemService.atualizar(id, request));
    }

    @Operation(summary = "Criar personagem", description = "Cadastra um novo personagem na plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Personagem criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @PostMapping
    public ResponseEntity<PersonagemResponse> criar(@Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemService.criar(request));
    }
}
