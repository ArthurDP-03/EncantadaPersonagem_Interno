package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.ClienteService;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteRequest;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteResponse;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

@GetMapping("/{id}")
public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(clienteService.buscarPorId(id));
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deletar(@PathVariable Integer id) {
    clienteService.deletar(id);
    return ResponseEntity.noContent().build();
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ClienteResponse> atualizar(
        @PathVariable Integer id,
        @Valid @RequestBody ClienteRequest request) {
    return ResponseEntity.ok(clienteService.atualizar(id, request));
}

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.criar(request));
    }
}
