package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.*;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.*;
import br.com.encantada.personageminterno.repository.*;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.convite.ConviteRespostaRequest;
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
class ConviteServiceTest {

    @Mock ConviteRepository conviteRepository;
    @Mock EventoPersonagemRepository epRepository;
    @Mock AtorRepository atorRepository;
    @Mock AdministradorRepository administradorRepository;

    @InjectMocks ConviteService conviteService;

    // ── helpers ──────────────────────────────────────────────────────────────

    private Administrador admin(Integer id, String email) {
        return Administrador.builder().id(id).nome("Admin").email(email).build();
    }

    private Ator ator(Integer id, String email) {
        return Ator.builder().id(id).nome("Ator " + id).email(email).build();
    }

    private Evento evento(EventoStatus status) {
        return Evento.builder()
                .id(1)
                .titulo("Festa")
                .status(status)
                .dataInicio(LocalDateTime.now().plusDays(10))
                .administradorCriador(admin(1, "admin@email.com"))
                .cliente(Cliente.builder().id(1).nome("Cliente").build())
                .build();
    }

    private EventoPersonagem ep(Integer id, EventoStatus status) {
        return EventoPersonagem.builder()
                .id(id)
                .evento(evento(status))
                .personagem(Personagem.builder().id(1).nome("Alice").build())
                .build();
    }

    private Convite convite(Integer id, ConviteStatus status, Ator ator, EventoPersonagem ep, Administrador adm) {
        return Convite.builder()
                .id(id)
                .status(status)
                .ator(ator)
                .eventoPersonagem(ep)
                .administrador(adm)
                .dataEnvio(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(7))
                .build();
    }

    // ── enviarConvites ───────────────────────────────────────────────────────

    @Test
    void enviarConvites_deveEnviarComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Ator ator1 = ator(10, "ator@email.com");
        ConviteCreateRequest req = new ConviteCreateRequest(1, List.of(10));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.empty());
        when(atorRepository.findById(10)).thenReturn(Optional.of(ator1));
        when(conviteRepository.save(any())).thenAnswer(inv -> {
            Convite c = inv.getArgument(0);
            return Convite.builder()
                    .id(99).status(c.getStatus()).ator(c.getAtor())
                    .eventoPersonagem(c.getEventoPersonagem())
                    .administrador(c.getAdministrador())
                    .dataEnvio(c.getDataEnvio()).dataExpiracao(c.getDataExpiracao()).build();
        });

        List<ConviteResponse> result = conviteService.enviarConvites(req, "admin@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(ConviteStatus.PENDENTE);
        verify(conviteRepository).save(any());
    }

    @Test
    void enviarConvites_devePularAtorJaConvidado() {
        Administrador adm = admin(1, "admin@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Ator ator1 = ator(10, "ator@email.com");
        Convite conviteExistente = convite(5, ConviteStatus.PENDENTE, ator1, ep, adm);
        ConviteCreateRequest req = new ConviteCreateRequest(1, List.of(10));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));
        when(conviteRepository.findByEventoPersonagemIdAndAtorId(1, 10)).thenReturn(Optional.of(conviteExistente));

        List<ConviteResponse> result = conviteService.enviarConvites(req, "admin@email.com");

        assertThat(result).isEmpty();
        verify(conviteRepository, never()).save(any());
    }

    @Test
    void enviarConvites_deveLancarBusinessQuandoEventoCancelado() {
        Administrador adm = admin(1, "admin@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CANCELADO);
        ConviteCreateRequest req = new ConviteCreateRequest(1, List.of(10));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> conviteService.enviarConvites(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CANCELADO");
    }

    @Test
    void enviarConvites_deveLancarBusinessQuandoEventoFinalizado() {
        Administrador adm = admin(1, "admin@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.FINALIZADO);
        ConviteCreateRequest req = new ConviteCreateRequest(1, List.of(10));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(1)).thenReturn(Optional.of(ep));

        assertThatThrownBy(() -> conviteService.enviarConvites(req, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("FINALIZADO");
    }

    @Test
    void enviarConvites_deveLancarNotFoundQuandoAdminNaoExiste() {
        ConviteCreateRequest req = new ConviteCreateRequest(1, List.of(10));
        when(administradorRepository.findByEmail("x@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.enviarConvites(req, "x@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void enviarConvites_deveLancarNotFoundQuandoEpNaoExiste() {
        Administrador adm = admin(1, "admin@email.com");
        ConviteCreateRequest req = new ConviteCreateRequest(99, List.of(10));

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(epRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.enviarConvites(req, "admin@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── responder ────────────────────────────────────────────────────────────

    @Test
    void responder_deveAceitarConviteComSucesso() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.ACEITO);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));
        when(conviteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConviteResponse result = conviteService.responder(1, req, "ator@email.com");

        assertThat(result.status()).isEqualTo(ConviteStatus.ACEITO);
    }

    @Test
    void responder_deveRecusarConviteComSucesso() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.RECUSADO);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));
        when(conviteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConviteResponse result = conviteService.responder(1, req, "ator@email.com");

        assertThat(result.status()).isEqualTo(ConviteStatus.RECUSADO);
    }

    @Test
    void responder_deveLancarForbiddenQuandoAtorErrado() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.ACEITO);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> conviteService.responder(1, req, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void responder_deveLancarBusinessQuandoConviteExpirado() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.EXPIRADO, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.ACEITO);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> conviteService.responder(1, req, "ator@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void responder_deveLancarBusinessQuandoConviteJaRespondido() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.ACEITO, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.ACEITO);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> conviteService.responder(1, req, "ator@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("respondido");
    }

    @Test
    void responder_deveLancarPreconditionQuandoStatusInvalido() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);
        ConviteRespostaRequest req = new ConviteRespostaRequest(ConviteStatus.PENDENTE);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> conviteService.responder(1, req, "ator@email.com"))
                .isInstanceOf(PreconditionFailedException.class);
    }

    // ── cancelar ─────────────────────────────────────────────────────────────

    @Test
    void cancelar_deveCancelarComSucesso() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(conviteRepository.save(any())).thenReturn(c);

        conviteService.cancelar(1, "admin@email.com");

        assertThat(c.getStatus()).isEqualTo(ConviteStatus.CANCELADO);
        verify(conviteRepository).save(c);
    }

    @Test
    void cancelar_deveLancarForbiddenQuandoOutroAdmin() {
        Administrador dono = admin(1, "admin@email.com");
        Administrador outro = admin(2, "outro@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, dono);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));
        when(administradorRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> conviteService.cancelar(1, "outro@email.com"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cancelar_deveLancarBusinessQuandoConviteNaoPendente() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Convite c = convite(1, ConviteStatus.ACEITO, ator1, ep, adm);

        when(conviteRepository.findById(1)).thenReturn(Optional.of(c));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));

        assertThatThrownBy(() -> conviteService.cancelar(1, "admin@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pendentes");
    }

    // ── listarMeus (ator) ────────────────────────────────────────────────────

    @Test
    void listarMeus_deveRetornarConvitesPendentesDoAtor() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);

        when(atorRepository.findByEmail("ator@email.com")).thenReturn(Optional.of(ator1));
        when(conviteRepository.findByAtorIdAndStatus(10, ConviteStatus.PENDENTE)).thenReturn(List.of(c));

        List<ConviteResponse> result = conviteService.listarMeus("ator@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).atorId()).isEqualTo(10);
    }

    @Test
    void listarMeus_deveLancarNotFoundQuandoAtorNaoExiste() {
        when(atorRepository.findByEmail("x@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.listarMeus("x@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── listarMeusConvites (admin) ───────────────────────────────────────────

    @Test
    void listarMeusConvites_deveRetornarConvitesDoAdmin() {
        Administrador adm = admin(1, "admin@email.com");
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);

        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adm));
        when(conviteRepository.findByAdministradorId(1)).thenReturn(List.of(c));

        List<ConviteResponse> result = conviteService.listarMeusConvites("admin@email.com");

        assertThat(result).hasSize(1);
    }

    // ── listarPorEventoPersonagem ────────────────────────────────────────────

    @Test
    void listarPorEventoPersonagem_deveRetornarConvites() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.PENDENTE, ator1, ep, adm);

        when(epRepository.existsById(1)).thenReturn(true);
        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of(c));

        List<ConviteResponse> result = conviteService.listarPorEventoPersonagem(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void listarPorEventoPersonagem_deveLancarNotFoundQuandoEpNaoExiste() {
        when(epRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> conviteService.listarPorEventoPersonagem(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── personagemIndisponivel ───────────────────────────────────────────────

    @Test
    void personagemIndisponivel_deveRetornarTrueQuandoTodosRecusaram() {
        Ator ator1 = ator(10, "ator@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c = convite(1, ConviteStatus.RECUSADO, ator1, ep, adm);

        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of(c));
        when(conviteRepository.countByEventoPersonagemIdAndStatus(1, ConviteStatus.RECUSADO)).thenReturn(1L);

        assertThat(conviteService.personagemIndisponivel(1)).isTrue();
    }

    @Test
    void personagemIndisponivel_deveRetornarFalseQuandoAindaHaPendente() {
        Ator ator1 = ator(10, "ator@email.com");
        Ator ator2 = ator(11, "ator2@email.com");
        EventoPersonagem ep = ep(1, EventoStatus.CONFIRMADO);
        Administrador adm = admin(1, "admin@email.com");
        Convite c1 = convite(1, ConviteStatus.RECUSADO, ator1, ep, adm);
        Convite c2 = convite(2, ConviteStatus.PENDENTE, ator2, ep, adm);

        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of(c1, c2));
        when(conviteRepository.countByEventoPersonagemIdAndStatus(1, ConviteStatus.RECUSADO)).thenReturn(1L);

        assertThat(conviteService.personagemIndisponivel(1)).isFalse();
    }

    @Test
    void personagemIndisponivel_deveRetornarFalseQuandoSemConvites() {
        when(conviteRepository.findByEventoPersonagemId(1)).thenReturn(List.of());
        when(conviteRepository.countByEventoPersonagemIdAndStatus(1, ConviteStatus.RECUSADO)).thenReturn(0L);

        assertThat(conviteService.personagemIndisponivel(1)).isFalse();
    }
}
