package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.security.JwtService;
import br.com.encantada.personageminterno.web.dto.auth.LoginRequest;
import br.com.encantada.personageminterno.web.dto.auth.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdministradorRepository administradorRepository;
    private final AtorRepository atorRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            AdministradorRepository administradorRepository,
            AtorRepository atorRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.administradorRepository = administradorRepository;
        this.atorRepository = atorRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        return administradorRepository.findByEmail(request.email())
                .map(administrador -> new LoginResponse(
                        jwtService.generateToken(administrador),
                        administrador.getNome(),
                        administrador.getEmail(),
                        normalizeRole(administrador.getTipo())
                ))
                .orElseGet(() -> atorRepository.findByEmail(request.email())
                        .map(ator -> new LoginResponse(
                                jwtService.generateToken(ator),
                                ator.getNome(),
                                ator.getEmail(),
                                "ATOR"
                        ))
                        .orElseThrow(() -> new IllegalStateException("Usuario autenticado nao encontrado")));
    }

    private String normalizeRole(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase().replace(' ', '_');
    }
}