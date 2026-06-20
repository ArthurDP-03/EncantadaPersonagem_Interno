package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.PersonagemItemService;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemRequest;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemResponse;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Personagem Item", description = "Gerenciamento do estoque de itens de personagens")
@RestController
@RequestMapping("/personagem-item")
public class PersonagemItemController {

    private final PersonagemItemService personagemItemService;

    public PersonagemItemController(PersonagemItemService personagemItemService) {
        this.personagemItemService = personagemItemService;
    }

    @Operation(summary = "Listar itens", description = "Retorna a lista de todos os itens de personagem cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<PersonagemItemResponse>> listar() {
        return ResponseEntity.ok(personagemItemService.listar());
    }

    @Operation(summary = "Buscar item por ID", description = "Retorna os dados de um item específico pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonagemItemResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(personagemItemService.buscarPorId(id));
    }

    @Operation(summary = "Criar novo item", description = "Adiciona um novo item de personagem ao estoque. O status inicial é sempre DISPONIVEL.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Personagem não encontrado"),
            @ApiResponse(responseCode = "409", description = "Código já cadastrado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonagemItemResponse> criar(@Valid @RequestBody PersonagemItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemItemService.criar(request));
    }

    @Operation(summary = "Atualizar status do item", description = "Altera o status de um item de personagem (DISPONIVEL, EM_USO, MANUTENCAO).")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonagemItemResponse> atualizarStatus(@PathVariable Integer id,
                                                                   @Valid @RequestBody PersonagemItemStatusRequest request) {
        return ResponseEntity.ok(personagemItemService.atualizarStatus(id, request));
    }

    @Operation(summary = "Remover item", description = "Remove um item de personagem do estoque.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        personagemItemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}