package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.EventoPersonagemService;
import br.com.encantada.personageminterno.service.EventoService;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.AdicionarPersonagemRequest;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Eventos", description = "Gerenciamento de eventos da plataforma")
@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final EventoPersonagemService eventoPersonagemService;

    public EventoController(EventoService eventoService, EventoPersonagemService eventoPersonagemService) {
        this.eventoService = eventoService;
        this.eventoPersonagemService = eventoPersonagemService;
    }

    @Operation(
            summary = "Listar eventos",
            description = "Retorna a lista de todos os eventos cadastrados."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<List<EventoResponse>> listar() {
        return ResponseEntity.ok(eventoService.listar());
    }

    @Operation(
            summary = "Buscar evento por ID",
            description = "Retorna os dados de um evento específico pelo seu identificador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @Operation(
            summary = "Criar evento",
            description = "Cria um novo evento no sistema."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
    })
    @PostMapping
    public ResponseEntity<EventoResponse> criar(
            @Valid @RequestBody EventoRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventoService.criar(request, authentication.getName()));
    }

    @Operation(summary = "Listar personagens do evento", description = "Retorna todos os personagens vinculados a um evento.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/{id}/personagens")
    public ResponseEntity<List<EventoPersonagemResponse>> listarPersonagens(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.listarPersonagens(id));
    }

    @Operation(
            summary = "Adicionar personagem ao evento",
            description = "Vincula um personagem a um evento existente. Verifica disponibilidade de figurino antes de inserir."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Personagem vinculado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sem figurinos disponíveis ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN e ser criador do evento"),
            @ApiResponse(responseCode = "404", description = "Evento ou personagem não encontrado"),
            @ApiResponse(responseCode = "409", description = "Personagem já vinculado ao evento")
    })
    @PostMapping("/{id}/personagens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoPersonagemResponse> adicionarPersonagem(
            @PathVariable Integer id,
            @Valid @RequestBody AdicionarPersonagemRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventoService.adicionarPersonagem(id, request, authentication.getName()));
    }

    @Operation(
            summary = "Remover personagem do evento",
            description = "Remove o vínculo entre um personagem e um evento. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personagem removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN e ser criador do evento"),
            @ApiResponse(responseCode = "404", description = "Evento ou evento-personagem não encontrado"),
            @ApiResponse(responseCode = "409", description = "Já existe escalação para este evento-personagem")
    })
    @DeleteMapping("/{id}/personagens/{epId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerPersonagem(
            @PathVariable Integer id,
            @PathVariable Integer epId,
            Authentication authentication) {
        eventoPersonagemService.removerPersonagem(id, epId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualizar evento",
            description = "Atualiza os dados de um evento existente. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EventoRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                eventoService.atualizar(id, request, authentication.getName())
        );
    }

    @Operation(
            summary = "Cancelar evento",
            description = "Cancela um evento existente. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento cancelado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> cancelar(
            @PathVariable Integer id,
            Authentication authentication) {

        return ResponseEntity.ok(
                eventoService.cancelar(id, authentication.getName())
        );
    }

    @Operation(
            summary = "Remover evento",
            description = "Remove um evento pelo ID. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(
            @PathVariable Integer id,
            Authentication authentication) {

        eventoService.deletar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}