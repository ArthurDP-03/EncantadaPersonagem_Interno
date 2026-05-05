package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.AtorService;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
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

@RestController
@RequestMapping("/atores")
public class AtorController {

    private final AtorService atorService;

    public AtorController(AtorService atorService) {
        this.atorService = atorService;
    }

    @GetMapping
    public ResponseEntity<List<AtorResponse>> listar() {
        return ResponseEntity.ok(atorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(atorService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAtorOwner(#id, authentication)")
    public ResponseEntity<Void> deletar(@PathVariable Integer id, Authentication authentication) {
        atorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isAtorOwner(#id, authentication)")
    public ResponseEntity<AtorResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AtorRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(atorService.atualizar(id, request));
    }

    @PostMapping
    public ResponseEntity<AtorResponse> criar(@Valid @RequestBody AtorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atorService.criar(request));
    }
}
