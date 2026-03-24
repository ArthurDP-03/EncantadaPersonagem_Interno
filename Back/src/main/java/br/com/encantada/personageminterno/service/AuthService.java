package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
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
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            AdministradorRepository administradorRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.administradorRepository = administradorRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        Administrador administrador = administradorRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Administrador autenticado nao encontrado"));

        return new LoginResponse(
                jwtService.generateToken(administrador),
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getTipo()
        );
    }
}
