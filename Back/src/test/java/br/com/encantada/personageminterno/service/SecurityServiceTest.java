package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private AtorRepository atorRepository;

    @InjectMocks
    private SecurityService securityService;

    // ── isOwner ───────────────────────────────────────────────────────────────

    @Test
    void isOwner_deveRetornarTrueQuandoAdminForDono() {
        var auth = new UsernamePasswordAuthenticationToken("admin@email.com", null);
        Administrador admin = Administrador.builder().id(1).email("admin@email.com").build();

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        assertThat(securityService.isOwner(1, auth)).isTrue();
    }

    @Test
    void isOwner_deveRetornarFalseQuandoAdminForDiferente() {
        var auth = new UsernamePasswordAuthenticationToken("admin@email.com", null);
        Administrador admin = Administrador.builder().id(2).email("admin@email.com").build();

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        assertThat(securityService.isOwner(1, auth)).isFalse();
    }

    @Test
    void isOwner_deveRetornarFalseQuandoAuthNulo() {
        assertThat(securityService.isOwner(1, null)).isFalse();
    }

    @Test
    void isOwner_deveRetornarFalseQuandoIdNulo() {
        var auth = new UsernamePasswordAuthenticationToken("admin@email.com", null);

        assertThat(securityService.isOwner(null, auth)).isFalse();
    }

    @Test
    void isOwner_deveRetornarFalseQuandoAdminNaoEncontrado() {
        var auth = new UsernamePasswordAuthenticationToken("inexistente@email.com", null);

        when(administradorRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThat(securityService.isOwner(1, auth)).isFalse();
    }

    // ── isAtorOwner ───────────────────────────────────────────────────────────

    @Test
    void isAtorOwner_deveRetornarTrueQuandoAtorForDono() {
        var auth = new UsernamePasswordAuthenticationToken("ator@email.com", null);
        Ator ator = Ator.builder().id(1).email("ator@email.com").build();

        when(atorRepository.findByEmail("ator@email.com")).thenReturn(Optional.of(ator));

        assertThat(securityService.isAtorOwner(1, auth)).isTrue();
    }

    @Test
    void isAtorOwner_deveRetornarFalseQuandoAtorForDiferente() {
        var auth = new UsernamePasswordAuthenticationToken("ator@email.com", null);
        Ator ator = Ator.builder().id(2).email("ator@email.com").build();

        when(atorRepository.findByEmail("ator@email.com")).thenReturn(Optional.of(ator));

        assertThat(securityService.isAtorOwner(1, auth)).isFalse();
    }

    @Test
    void isAtorOwner_deveRetornarFalseQuandoAuthNulo() {
        assertThat(securityService.isAtorOwner(1, null)).isFalse();
    }

    @Test
    void isAtorOwner_deveRetornarFalseQuandoIdNulo() {
        var auth = new UsernamePasswordAuthenticationToken("ator@email.com", null);

        assertThat(securityService.isAtorOwner(null, auth)).isFalse();
    }

    @Test
    void isAtorOwner_deveRetornarFalseQuandoAtorNaoEncontrado() {
        var auth = new UsernamePasswordAuthenticationToken("inexistente@email.com", null);

        when(atorRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThat(securityService.isAtorOwner(1, auth)).isFalse();
    }

    // ── isAdmin ───────────────────────────────────────────────────────────────

    @Test
    void isAdmin_deveRetornarTrueQuandoPossuiRoleAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin@email.com", null, List.of(() -> "ROLE_ADMIN"));

        assertThat(securityService.isAdmin(auth)).isTrue();
    }

    @Test
    void isAdmin_deveRetornarFalseQuandoPossuiApenasRoleAtor() {
        var auth = new UsernamePasswordAuthenticationToken(
                "ator@email.com", null, List.of(() -> "ROLE_ATOR"));

        assertThat(securityService.isAdmin(auth)).isFalse();
    }

    @Test
    void isAdmin_deveRetornarFalseQuandoAuthNulo() {
        assertThat(securityService.isAdmin(null)).isFalse();
    }

    @Test
    void isAdmin_deveRetornarFalseQuandoSemAuthorities() {
        var auth = new UsernamePasswordAuthenticationToken(
                "usuario@email.com", null, List.of());

        assertThat(securityService.isAdmin(auth)).isFalse();
    }
}
