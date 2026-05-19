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
import org.springframework.web.bind.annotation.RestController;

import br.com.encantada.personageminterno.service.ConviteService;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.convite.ConviteRespostaRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> enviarConvites(
            @Valid @RequestBody ConviteCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conviteService.enviarConvites(request, authentication.getName()));
    }

    @GetMapping("/evento-personagem/{epId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConviteResponse>> listarPorEventoPersonagem(@PathVariable Integer epId) {
        return ResponseEntity.ok(conviteService.listarPorEventoPersonagem(epId));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<List<ConviteResponse>> listarMeus(Authentication authentication) {
        return ResponseEntity.ok(conviteService.listarMeus(authentication.getName()));
    }

    @PatchMapping("/{id}/responder")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<ConviteResponse> responder(
            @PathVariable Integer id,
            @Valid @RequestBody ConviteRespostaRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(conviteService.responder(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id, Authentication authentication) {
        conviteService.cancelar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
