package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.*;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.*;
import br.com.encantada.personageminterno.repository.*;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.escalacao.TrocarPersonagemRequest;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.ReabrirConvitesRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoPersonagemServiceTest {

    @Mock EventoPersonagemRepository epRepository;
    @Mock PersonagemRepository personagemRepository;
    @Mock ConviteRepository conviteRepository;
    @Mock EscalacaoRepository escalacaoRepository;
    @Mock AdministradorRepository administradorRepository;
    @Mock ConviteService conviteService;

    @InjectMocks EventoPersonagemService eventoPersonagemService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Administrador admin(Integer id, String email) {
        return Administrador.builder().id(id).nome("Admin").email(email).build();
    }

    private Ator ator(Integer id) {
        return Ator.builder().id(id).nome("Ator " + id).email("ator" + id + "@email.com").build();
    }

    private Personagem personagem(Integer id, String nome) {
        return Personagem.builder().id(id).nome(nome).build();
    }

    private Evento evento(Integer adminId, String adminEmail, EventoStatus status) {
        return Evento.builder()
                .id(1).titulo("Festa").status(status)
                .dataInicio(LocalDateTime.now().plusDays(10))
                .administradorCriador(admin(adminId, adminEmail))
                .cliente(Cliente.builder().id(1).nome("Cliente").build())
                .build();
    }

    private EventoPersonagem ep(Integer id, Personagem p, EventoStatus status, String adminEmail) {
        return EventoPersonagem.builder()
                .id(id)
                .evento(evento(1, adminEmail, status))
                .personagem(p)
                .build();
    }

    private Convite convite(Integer id, ConviteStatus status, Ator ator, EventoPersonagem ep) {
        return Convite.builder()
                .id(id).status(status).ator(ator).eventoPersonagem(ep)
                .administrador(admin(1, "admin@email.com"))
                .dataEnvio(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(7))
                .build();
    }

    // ── trocarPersonagem ──────────────────────────────────────────────────────

    @Test
    void trocarPersonagem_deveTrocarComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem pAtual = personagem(1, "Alice");
        Personagem pNovo = personagem(2, "Cinderela");
        EventoPersonagem ep = ep(1, pAtual, EventoStatus.CONFIRMADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(true);
        when(epRepository.existsByEventoIdAndPersonagemId(1, 2)).thenReturn(false);
        when(personagemRepository.findById(2)).thenReturn(Optional.of(pNovo));
        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of());
        when(epRepository.save(ep)).thenReturn(ep);

        EventoPersonagemResponse result = eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com");

        assertThat(result.personagemNome()).isEqualTo("Cinderela");
        assertThat(ep.getPersonagem()).isEqualTo(pNovo);
        verify(epRepository).save(ep);
    }

    @Test
    void trocarPersonagem_deveDeletarConvitesAntigosAoTrocar() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem pAtual = personagem(1, "Alice");
        Personagem pNovo = personagem(2, "Cinderela");
        EventoPersonagem ep = ep(1, pAtual, EventoStatus.CONFIRMADO, "admin@email.com");
        Ator ator1 = ator(10);
        Convite c = convite(1, ConviteStatus.RECUSADO, ator1, ep);
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(true);
        when(epRepository.existsByEventoIdAndPersonagemId(1, 2)).thenReturn(false);
        when(personagemRepository.findById(2)).thenReturn(Optional.of(pNovo));
        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of(c));
        when(epRepository.save(ep)).thenReturn(ep);

        eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com");

        verify(conviteRepository).deleteAll(List.of(c));
    }

    @Test
    void trocarPersonagem_deveLancarForbiddenQuandoOutroAdmin() {
        Administrador adm = admin(1, "admin@email.com");
        Administrador outro = admin(2, "outro@email.com");
        Personagem p = personagem(1, "Alice");
        // evento criado pelo admin 1, mas requisição vem do admin 2
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void trocarPersonagem_deveLancarConflictQuandoJaExisteEscalacao() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(true);

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void trocarPersonagem_deveLancarBusinessQuandoAindaHaConvitesPendentes() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(false);

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pendentes");
    }

    @Test
    void trocarPersonagem_deveLancarBusinessQuandoMesmoPersonagem() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        // novo personagem igual ao atual (id 1)
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(1);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(true);

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("diferente");
    }

    @Test
    void trocarPersonagem_deveLancarConflictQuandoPersonagemJaVinculadoAoEvento() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(true);
        when(epRepository.existsByEventoIdAndPersonagemId(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void trocarPersonagem_deveLancarBusinessQuandoEventoCancelado() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CANCELADO, "admin@email.com");
        TrocarPersonagemRequest req = new TrocarPersonagemRequest(2);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> eventoPersonagemService.trocarPersonagem(1, req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CANCELADO");
    }

    // ── reabrirConvites ───────────────────────────────────────────────────────

    @Test
    void reabrirConvites_deveReabrirComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        Ator ator1 = ator(10);
        Convite cAntigo = convite(1, ConviteStatus.RECUSADO, ator1, ep);
        ReabrirConvitesRequest req = new ReabrirConvitesRequest(List.of(20));

        ConviteResponse novoConvite = new ConviteResponse(
                2, 1, 1, "Festa", 1, "Alice", 20, "Ator 20",
                1, ConviteStatus.PENDENTE, LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), null);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(true);
        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of(cAntigo));
        when(conviteService.enviarConvites(any(ConviteCreateRequest.class), eq("admin@email.com")))
                .thenReturn(List.of(novoConvite));

        List<ConviteResponse> result = eventoPersonagemService.reabrirConvites(1, req, "admin@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(ConviteStatus.PENDENTE);
        verify(conviteRepository).deleteAll(List.of(cAntigo));
    }

    @Test
    void reabrirConvites_deveLancarConflictQuandoJaExisteEscalacao() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        ReabrirConvitesRequest req = new ReabrirConvitesRequest(List.of(20));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(true);

        assertThatThrownBy(() -> eventoPersonagemService.reabrirConvites(1, req, "admin@email.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void reabrirConvites_deveLancarBusinessQuandoAindaHaConvitesPendentes() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        ReabrirConvitesRequest req = new ReabrirConvitesRequest(List.of(20));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(escalacaoRepository.existsByEventoPersonagemId(1)).thenReturn(false);
        when(conviteService.personagemIndisponivel(1)).thenReturn(false);

        assertThatThrownBy(() -> eventoPersonagemService.reabrirConvites(1, req, "admin@email.com"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void reabrirConvites_deveLancarForbiddenQuandoOutroAdmin() {
        Administrador outro = admin(2, "outro@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.CONFIRMADO, "admin@email.com");
        ReabrirConvitesRequest req = new ReabrirConvitesRequest(List.of(20));

        when(administradorRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> eventoPersonagemService.reabrirConvites(1, req, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void reabrirConvites_deveLancarBusinessQuandoEventoFinalizado() {
        Administrador adm = admin(1, "admin@email.com");
        Personagem p = personagem(1, "Alice");
        EventoPersonagem ep = ep(1, p, EventoStatus.FINALIZADO, "admin@email.com");
        ReabrirConvitesRequest req = new ReabrirConvitesRequest(List.of(20));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> eventoPersonagemService.reabrirConvites(1, req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("FINALIZADO");
    }
}
