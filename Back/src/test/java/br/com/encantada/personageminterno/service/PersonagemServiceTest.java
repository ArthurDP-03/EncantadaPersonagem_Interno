package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
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
class PersonagemServiceTest {

    @Mock
    private PersonagemRepository personagemRepository;

    @InjectMocks
    private PersonagemService personagemService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Personagem personagem(Integer id, String nome) {
        return Personagem.builder().id(id).nome(nome).descricao("Desc").foto("foto.jpg").build();
    }

    private PersonagemRequest request(String nome) {
        return new PersonagemRequest(nome, "Descrição", "foto.jpg");
    }

    // ── listar ────────────────────────────────────────────────────────────────

    @Test
    void listar_deveRetornarTodosOsPersonagens() {
        when(personagemRepository.findAll())
                .thenReturn(List.of(personagem(1, "Alice"), personagem(2, "Cinderela")));

        List<PersonagemResponse> result = personagemService.listar();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nome()).isEqualTo("Alice");
        assertThat(result.get(1).nome()).isEqualTo("Cinderela");
    }

    @Test
    void listar_deveRetornarListaVaziaQuandoNaoHaPersonagens() {
        when(personagemRepository.findAll()).thenReturn(List.of());

        assertThat(personagemService.listar()).isEmpty();
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    void buscarPorId_deveRetornarPersonagemExistente() {
        when(personagemRepository.findById(1)).thenReturn(Optional.of(personagem(1, "Alice")));

        PersonagemResponse result = personagemService.buscarPorId(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.nome()).isEqualTo("Alice");
        assertThat(result.descricao()).isEqualTo("Desc");
        assertThat(result.foto()).isEqualTo("foto.jpg");
    }

    @Test
    void buscarPorId_deveLancarNotFoundQuandoNaoExiste() {
        when(personagemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personagemService.buscarPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── criar ─────────────────────────────────────────────────────────────────

    @Test
    void criar_deveSalvarComSucesso() {
        PersonagemRequest req = request("Alice");
        when(personagemRepository.save(any())).thenReturn(personagem(1, "Alice"));

        PersonagemResponse result = personagemService.criar(req);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.nome()).isEqualTo("Alice");
        verify(personagemRepository).save(any());
    }

    @Test
    void criar_devePersistirTodosOsCampos() {
        PersonagemRequest req = new PersonagemRequest("Alice", "Personagem mágico", "alice.jpg");
        Personagem salvo = Personagem.builder().id(1).nome("Alice")
                .descricao("Personagem mágico").foto("alice.jpg").build();

        when(personagemRepository.save(any())).thenReturn(salvo);

        PersonagemResponse result = personagemService.criar(req);

        assertThat(result.descricao()).isEqualTo("Personagem mágico");
        assertThat(result.foto()).isEqualTo("alice.jpg");
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    void atualizar_deveAtualizarComSucesso() {
        Personagem existente = personagem(1, "Alice");
        PersonagemRequest req = new PersonagemRequest("Alice Atualizada", "Nova desc", "nova-foto.jpg");

        when(personagemRepository.findById(1)).thenReturn(Optional.of(existente));
        when(personagemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PersonagemResponse result = personagemService.atualizar(1, req);

        assertThat(result.nome()).isEqualTo("Alice Atualizada");
        assertThat(result.descricao()).isEqualTo("Nova desc");
        assertThat(result.foto()).isEqualTo("nova-foto.jpg");
        verify(personagemRepository).save(existente);
    }

    @Test
    void atualizar_deveLancarNotFoundQuandoNaoExiste() {
        when(personagemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personagemService.atualizar(99, request("Alice")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(personagemRepository, never()).save(any());
    }

    // ── deletar ───────────────────────────────────────────────────────────────

    @Test
    void deletar_deveDeletarComSucesso() {
        when(personagemRepository.existsById(1)).thenReturn(true);

        personagemService.deletar(1);

        verify(personagemRepository).deleteById(1);
    }

    @Test
    void deletar_deveLancarNotFoundQuandoNaoExiste() {
        when(personagemRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> personagemService.deletar(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(personagemRepository, never()).deleteById(any());
    }
}
