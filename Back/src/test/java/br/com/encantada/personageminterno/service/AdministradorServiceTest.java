package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private AtorRepository atorRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministradorService administradorService;

    // ── helpers ──────────────────────────────────────────────────────────────

    private Administrador adminComId(Integer id, String email) {
        return Administrador.builder()
                .id(id)
                .nome("Admin " + id)
                .email(email)
                .senha("hash")
                .telefone("41999990000")
                .build();
    }

    private AdministradorRequest request(String email) {
        return new AdministradorRequest("Nome Teste", email, "senha123", "41999990000");
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void listar_deveRetornarListaDeTodos() {
        when(administradorRepository.findAll())
                .thenReturn(List.of(
                        adminComId(1, "a@email.com"),
                        adminComId(2, "b@email.com")));

        List<AdministradorResponse> resultado = administradorService.listar();

        assertThat(resultado).hasSize(2);
        // Verifica os dois elementos para garantir mapeamento completo
        assertThat(resultado.get(0).email()).isEqualTo("a@email.com");
        assertThat(resultado.get(1).email()).isEqualTo("b@email.com");
    }

    @Test
    void listar_deveRetornarListaVaziaQuandoNaoHaAdministradores() {
        when(administradorRepository.findAll()).thenReturn(List.of());

        assertThat(administradorService.listar()).isEmpty();
    }

    // ── criar ────────────────────────────────────────────────────────────────

    @Test
    void criar_deveSalvarComSucesso() {
        AdministradorRequest req = request("novo@email.com");
        Administrador salvo = adminComId(1, "novo@email.com");

        when(administradorRepository.existsByEmail(req.email())).thenReturn(false);
        when(atorRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.senha())).thenReturn("hash");
        when(administradorRepository.save(any())).thenReturn(salvo);

        AdministradorResponse resultado = administradorService.criar(req);

        assertThat(resultado.email()).isEqualTo("novo@email.com");
        assertThat(resultado.id()).isEqualTo(1);
        verify(administradorRepository).save(any());
    }

    @Test
    void criar_deveCodificarSenhaAoSalvar() {
        AdministradorRequest req = request("novo@email.com");
        Administrador salvo = adminComId(1, "novo@email.com");

        when(administradorRepository.existsByEmail(req.email())).thenReturn(false);
        when(atorRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_encoded");
        when(administradorRepository.save(any())).thenReturn(salvo);

        administradorService.criar(req);

        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void criar_deveLancarConflictQuandoEmailJaExisteComoAdministrador() {
        AdministradorRequest req = request("existente@email.com");
        when(administradorRepository.existsByEmail(req.email())).thenReturn(true);

        assertThatThrownBy(() -> administradorService.criar(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("administrador");

        verify(administradorRepository, never()).save(any());
    }

    @Test
    void criar_deveLancarConflictQuandoEmailJaExisteComoAtor() {
        AdministradorRequest req = request("ator@email.com");
        when(administradorRepository.existsByEmail(req.email())).thenReturn(false);
        when(atorRepository.existsByEmail(req.email())).thenReturn(true);

        assertThatThrownBy(() -> administradorService.criar(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ator");

        verify(administradorRepository, never()).save(any());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void buscarPorId_deveRetornarAdministradorExistente() {
        when(administradorRepository.findById(1))
                .thenReturn(Optional.of(adminComId(1, "a@email.com")));

        AdministradorResponse resultado = administradorService.buscarPorId(1);

        assertThat(resultado.id()).isEqualTo(1);
        assertThat(resultado.email()).isEqualTo("a@email.com");
        assertThat(resultado.nome()).isEqualTo("Admin 1");
    }

    @Test
    void buscarPorId_deveLancarResourceNotFoundQuandoNaoExiste() {
        when(administradorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administradorService.buscarPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── atualizar ────────────────────────────────────────────────────────────

    @Test
    void atualizar_deveAtualizarComSucesso() {
        Administrador admin = adminComId(1, "admin@email.com");
        AdministradorRequest req = request("admin@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(administradorRepository.save(any())).thenReturn(admin);

        AdministradorResponse resultado = administradorService.atualizar(1, req, "admin@email.com");

        // Verificações concretas do retorno
        assertThat(resultado.email()).isEqualTo("admin@email.com");
        assertThat(resultado.nome()).isEqualTo("Nome Teste");
        verify(administradorRepository).save(admin);
    }

    @Test
    void atualizar_deveLancarResourceNotFoundQuandoAdminNaoExiste() {
        AdministradorRequest req = request("qualquer@email.com");
        when(administradorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administradorService.atualizar(99, req, "logado@email.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(administradorRepository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarForbiddenQuandoTentaEditarOutroAdmin() {
        Administrador alvo   = adminComId(1, "alvo@email.com");
        Administrador logado = adminComId(2, "logado@email.com");
        AdministradorRequest req = request("alvo@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(alvo));
        when(administradorRepository.findByEmail("logado@email.com")).thenReturn(Optional.of(logado));

        assertThatThrownBy(() -> administradorService.atualizar(1, req, "logado@email.com"))
                .isInstanceOf(ForbiddenException.class);

        verify(administradorRepository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarConflictQuandoNovoEmailJaUsadoPorOutroAdmin() {
        Administrador admin = adminComId(1, "admin@email.com");
        AdministradorRequest req = request("outro@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(administradorRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThatThrownBy(() -> administradorService.atualizar(1, req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void atualizar_deveLancarConflictQuandoNovoEmailJaUsadoPorAtor() {
        Administrador admin = adminComId(1, "admin@email.com");
        AdministradorRequest req = request("ator@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(administradorRepository.existsByEmail("ator@email.com")).thenReturn(false);
        when(atorRepository.existsByEmail("ator@email.com")).thenReturn(true);

        assertThatThrownBy(() -> administradorService.atualizar(1, req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void atualizar_deveCodificarSenhaQuandoFornecida() {
        Administrador admin = adminComId(1, "admin@email.com");
        AdministradorRequest req = new AdministradorRequest("Nome", "admin@email.com", "novaSenha", "41999990000");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novoHash");
        when(administradorRepository.save(any())).thenReturn(admin);

        administradorService.atualizar(1, req, "admin@email.com");

        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    void atualizar_naoDeveCodificarSenhaQuandoNaoFornecida() {
        Administrador admin = adminComId(1, "admin@email.com");
        AdministradorRequest req = new AdministradorRequest("Nome", "admin@email.com", "", "41999990000");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(administradorRepository.save(any())).thenReturn(admin);

        administradorService.atualizar(1, req, "admin@email.com");

        verify(passwordEncoder, never()).encode(any());
    }

    // ── deletar ──────────────────────────────────────────────────────────────

    @Test
    void deletar_deveDeletarComSucesso() {
        Administrador alvo   = adminComId(1, "alvo@email.com");
        Administrador logado = adminComId(2, "logado@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(alvo));
        when(administradorRepository.findByEmail("logado@email.com")).thenReturn(Optional.of(logado));

        administradorService.deletar(1, "logado@email.com");

        // Valor exato ao invés de any() para evitar falsos positivos com primitivos
        verify(administradorRepository).deleteById(1);
    }

    @Test
    void deletar_deveLancarForbiddenQuandoTentaDeletarPropriaConta() {
        Administrador admin = adminComId(1, "admin@email.com");

        when(administradorRepository.findById(1)).thenReturn(Optional.of(admin));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> administradorService.deletar(1, "admin@email.com"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("própria conta");

        verify(administradorRepository, never()).deleteById(anyInt());
    }

    @Test
    void deletar_deveLancarResourceNotFoundQuandoAdminNaoExiste() {
        when(administradorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administradorService.deletar(99, "logado@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(administradorRepository, never()).deleteById(anyInt());
    }
}
