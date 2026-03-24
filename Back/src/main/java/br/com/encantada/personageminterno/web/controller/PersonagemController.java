package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.PersonagemService;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<PersonagemResponse> criar(@Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemService.criar(request));
    }
}
