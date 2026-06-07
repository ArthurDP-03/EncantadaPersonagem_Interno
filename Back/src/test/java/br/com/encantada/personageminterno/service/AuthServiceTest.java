package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.security.JwtService;
import br.com.encantada.personageminterno.web.dto.auth.LoginRequest;
import br.com.encantada.personageminterno.web.dto.auth.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private AtorRepository atorRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginComoAdministrador() {

        LoginRequest request =
                new LoginRequest(
                        "admin@email.com",
                        "123456"
                );

        Administrador admin = Administrador.builder()
                .id(1)
                .nome("Administrador")
                .email("admin@email.com")
                .build();

        when(administradorRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(admin));

        when(jwtService.generateToken(admin))
                .thenReturn("token-admin");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-admin", response.token());
        assertEquals("Administrador", response.nome());
        assertEquals("admin@email.com", response.email());
        assertEquals("ADMIN", response.tipo());

        verify(authenticationManager)
                .authenticate(any());
    }

    @Test
    void deveRealizarLoginComoAtor() {

        LoginRequest request =
                new LoginRequest(
                        "ator@email.com",
                        "123456"
                );

        Ator ator = Ator.builder()
                .id(1)
                .nome("João")
                .email("ator@email.com")
                .build();

        when(administradorRepository.findByEmail("ator@email.com"))
                .thenReturn(Optional.empty());

        when(atorRepository.findByEmail("ator@email.com"))
                .thenReturn(Optional.of(ator));

        when(jwtService.generateToken(ator))
                .thenReturn("token-ator");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-ator", response.token());
        assertEquals("João", response.nome());
        assertEquals("ator@email.com", response.email());
        assertEquals("ATOR", response.tipo());

        verify(authenticationManager)
                .authenticate(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        LoginRequest request =
                new LoginRequest(
                        "inexistente@email.com",
                        "123456"
                );

        when(administradorRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        when(atorRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Usuario autenticado nao encontrado",
                exception.getMessage()
        );
    }
}