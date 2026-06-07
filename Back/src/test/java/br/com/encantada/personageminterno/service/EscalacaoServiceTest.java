package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.*;
import br.com.encantada.personageminterno.domain.enums.*;
import br.com.encantada.personageminterno.exception.*;
import br.com.encantada.personageminterno.repository.*;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoCreateRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalacaoServiceTest {

    @Mock EscalacaoRepository escalacaoRepository;
    @Mock EventoPersonagemRepository epRepository;
    @Mock ConviteRepository conviteRepository;
    @Mock PersonagemItemRepository personagemItemRepository;
    @Mock AdministradorRepository administradorRepository;

    @InjectMocks EscalacaoService escalacaoService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Administrador admin(Integer id, String email) {
        return Administrador.builder().id(id).nome("Admin").email(email).build();
    }

    private Ator ator(Integer id, String email) {
        return Ator.builder().id(id).nome("Ator " + id).email(email).build();
    }

    private Personagem personagem(Integer id) {
        return Personagem.builder().id(id).nome("Personagem " + id).build();
    }

    private Evento evento() {
        return Evento.builder()
                .id(1).titulo("Festa").status(EventoStatus.CONFIRMADO)
                .dataInicio(LocalDateTime.now().plusDays(5))
                .administradorCriador(admin(1, "admin@email.com"))
                .cliente(Cliente.builder().id(1).nome("Cliente").build())
                .build();
    }

    private EventoPersonagem ep(Integer id, Personagem p) {
        return EventoPersonagem.builder().id(id).evento(evento()).personagem(p).build();
    }

    private PersonagemItem item(Integer id, Personagem p, PersonagemItemStatus status) {
        return PersonagemItem.builder().id(id).personagem(p).codigo("COD-00" + id).status(status).build();
    }

    private Convite convite(Ator ator, EventoPersonagem ep, ConviteStatus status) {
        return Convite.builder()
                .id(1).ator(ator).eventoPersonagem(ep).status(status)
                .administrador(admin(1, "admin@email.com"))
                .dataEnvio(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(7))
                .build();
    }

    private Escalacao escalacao(Integer id, Ator ator, EventoPersonagem ep,
                                PersonagemItem pi, EscalacaoStatus status) {
        return Escalacao.builder()
                .id(id).ator(ator).eventoPersonagem(ep).personagemItem(pi)
                .administrador(admin(1, "admin@email.com"))
                .status(status).dataEscolha(LocalDateTime.now())
                .build();
    }

    // ── escolherAtorFinal ─────────────────────────────────────────────────────

    @Test
    void escolherAtorFinal_deveEscalarComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.DISPONIVEL);
        Convite c = convite(ator1, ep, ConviteStatus.ACEITO);

        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.of(c));
        when(personagemItemRepository.findById(5)).thenReturn(Optional.of(pi));
        when(personagemItemRepository.save(pi)).thenReturn(pi);
        when(escalacaoRepository.save(any())).thenAnswer(inv -> {
            Escalacao e = inv.getArgument(0);
            return Escalacao.builder()
                    .id(99).ator(e.getAtor()).eventoPersonagem(e.getEventoPersonagem())
                    .personagemItem(e.getPersonagemItem()).administrador(e.getAdministrador())
                    .status(e.getStatus()).dataEscolha(e.getDataEscolha()).build();
        });

        EscalacaoResponse result = escalacaoService.escolherAtorFinal(req, "admin@email.com");

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR);
        assertThat(pi.getStatus()).isEqualTo(PersonagemItemStatus.EM_USO);
        verify(personagemItemRepository).save(pi);
    }

    @Test
    void escolherAtorFinal_deveLancarConflictQuandoJaExisteEscalacao() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(true);

        assertThatThrownBy(() -> escalacaoService.escolherAtorFinal(req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void escolherAtorFinal_deveLancarBusinessQuandoAtorNaoConvidado() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> escalacaoService.escolherAtorFinal(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("convidado");
    }

    @Test
    void escolherAtorFinal_deveLancarBusinessQuandoAtorNaoAceitou() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        Convite c = convite(ator1, ep, ConviteStatus.PENDENTE);
        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> escalacaoService.escolherAtorFinal(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("aceitou");
    }

    @Test
    void escolherAtorFinal_deveLancarBusinessQuandoItemNaoPertenceAoPersonagem() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p1 = personagem(1);
        Personagem p2 = personagem(2);
        EventoPersonagem ep = ep(1, p1);
        // item pertence ao personagem 2, não ao 1
        PersonagemItem pi = item(5, p2, PersonagemItemStatus.DISPONIVEL);
        Convite c = convite(ator1, ep, ConviteStatus.ACEITO);
        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.of(c));
        when(personagemItemRepository.findById(5)).thenReturn(Optional.of(pi));

        assertThatThrownBy(() -> escalacaoService.escolherAtorFinal(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pertence");
    }

    @Test
    void escolherAtorFinal_deveLancarBusinessQuandoItemIndisponivel() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Convite c = convite(ator1, ep, ConviteStatus.ACEITO);
        EscalacaoCreateRequest req = new EscalacaoCreateRequest(1, 10, 5);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.of(c));
        when(personagemItemRepository.findById(5)).thenReturn(Optional.of(pi));

        assertThatThrownBy(() -> escalacaoService.escolherAtorFinal(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("indisponível");
    }

    // ── confirmarPresenca ─────────────────────────────────────────────────────

    @Test
    void confirmarPresenca_deveConfirmarComSucesso() {
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));
        when(escalacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EscalacaoResponse result = escalacaoService.confirmarPresenca(1, "ator@email.com");

        assertThat(result.status()).isEqualTo(EscalacaoStatus.CONFIRMADA);
        assertThat(e.getDataConfirmacaoAtor()).isNotNull();
    }

    @Test
    void confirmarPresenca_deveLancarForbiddenQuandoOutroAtor() {
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));

        assertThatThrownBy(() -> escalacaoService.confirmarPresenca(1, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void confirmarPresenca_deveLancarBusinessQuandoStatusIncorreto() {
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.CONFIRMADA);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));

        assertThatThrownBy(() -> escalacaoService.confirmarPresenca(1, "ator@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("confirmação");
    }

    @Test
    void confirmarPresenca_deveLancarNotFoundQuandoEscalacaoNaoExiste() {
        when(escalacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> escalacaoService.confirmarPresenca(99, "ator@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── cancelar ──────────────────────────────────────────────────────────────

    @Test
    void cancelar_deveCancelarELiberarItemComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.CONFIRMADA);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(personagemItemRepository.save(pi)).thenReturn(pi);
        when(escalacaoRepository.save(e)).thenReturn(e);

        escalacaoService.cancelar(1, "admin@email.com");

        assertThat(e.getStatus()).isEqualTo(EscalacaoStatus.CANCELADA);
        assertThat(pi.getStatus()).isEqualTo(PersonagemItemStatus.DISPONIVEL);
        verify(personagemItemRepository).save(pi);
    }

    @Test
    void cancelar_deveLancarForbiddenQuandoOutroAdmin() {
        Administrador dono = admin(1, "admin@email.com");
        Administrador outro = admin(2, "outro@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.CONFIRMADA);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));
        when(administradorRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> escalacaoService.cancelar(1, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cancelar_deveLancarBusinessQuandoJaCancelada() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.DISPONIVEL);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.CANCELADA);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));

        assertThatThrownBy(() -> escalacaoService.cancelar(1, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelada");
    }

    @Test
    void cancelar_naoDeveLiberarItemQuandoNaoEstaEmUso() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        // item já disponível (não deve ser re-salvo)
        PersonagemItem pi = item(5, p, PersonagemItemStatus.DISPONIVEL);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(escalacaoRepository.save(e)).thenReturn(e);

        escalacaoService.cancelar(1, "admin@email.com");

        verify(personagemItemRepository, never()).save(any());
        assertThat(e.getStatus()).isEqualTo(EscalacaoStatus.CANCELADA);
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    void buscarPorId_deveRetornarEscalacaoExistente() {
        Ator ator1 = ator(10, "ator@email.com");
        Personagem p = personagem(1);
        EventoPersonagem ep = ep(1, p);
        PersonagemItem pi = item(5, p, PersonagemItemStatus.EM_USO);
        Escalacao e = escalacao(1, ator1, ep, pi, EscalacaoStatus.CONFIRMADA);

        when(escalacaoRepository.findById(1)).thenReturn(Optional.of(e));

        EscalacaoResponse result = escalacaoService.buscarPorId(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.atorNome()).isEqualTo("Ator 10");
    }

    @Test
    void buscarPorId_deveLancarNotFoundQuandoNaoExiste() {
        when(escalacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> escalacaoService.buscarPorId(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
