package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.AdministradorService;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
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

@RestController
@RequestMapping("/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponse>> listar() {
        return ResponseEntity.ok(administradorService.listar());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(administradorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AdministradorRequest request,
            Authentication authentication) {
        String administradorEmail = authentication.getName();
        return ResponseEntity.ok(administradorService.atualizar(id, request, administradorEmail));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ATOR') and @securityService.isOwner(#id, authentication))")
    public ResponseEntity<Void> deletar(@PathVariable Integer id, Authentication authentication) {
        String administradorEmail = authentication.getName();
        administradorService.deletar(id, administradorEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<AdministradorResponse> criar(@Valid @RequestBody AdministradorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.criar(request));
    }
}
