package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.PersonagemService;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
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

@RestController
@RequestMapping("/personagens")
public class PersonagemController {

    private final PersonagemService personagemService;

    public PersonagemController(PersonagemService personagemService) {
        this.personagemService = personagemService;
    }

    @GetMapping
    public ResponseEntity<List<PersonagemResponse>> listar() {
        return ResponseEntity.ok(personagemService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonagemResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(personagemService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        personagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonagemResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.ok(personagemService.atualizar(id, request));
    }

    @PostMapping
    public ResponseEntity<PersonagemResponse> criar(@Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemService.criar(request));
    }
}
