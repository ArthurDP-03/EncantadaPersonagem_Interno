package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.AtorService;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
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

    @PostMapping
    public ResponseEntity<AtorResponse> criar(@Valid @RequestBody AtorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atorService.criar(request));
    }
}
