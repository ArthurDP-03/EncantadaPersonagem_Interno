package br.com.encantada.personageminterno.web.controller;

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

import br.com.encantada.personageminterno.service.EscalacaoService;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoCreateRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/escalacoes")
public class EscalacaoController {

    private final EscalacaoService escalacaoService;

    public EscalacaoController(EscalacaoService escalacaoService) {
        this.escalacaoService = escalacaoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EscalacaoResponse> escolherAtorFinal(
            @Valid @RequestBody EscalacaoCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(escalacaoService.escolherAtorFinal(request, authentication.getName()));
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ATOR')")
    public ResponseEntity<EscalacaoResponse> confirmarPresenca(
            @PathVariable Integer id,
            Authentication authentication) {
        return ResponseEntity.ok(escalacaoService.confirmarPresenca(id, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id, Authentication authentication) {
        escalacaoService.cancelar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscalacaoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(escalacaoService.buscarPorId(id));
    }
}
