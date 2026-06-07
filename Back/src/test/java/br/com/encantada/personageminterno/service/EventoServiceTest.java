package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Cliente;
import br.com.encantada.personageminterno.domain.entity.Evento;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.PreconditionFailedException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private AdministradorRepository administradorRepository;

    @InjectMocks
    private EventoService service;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Administrador admin(Integer id, String email) {
        return Administrador.builder().id(id).nome("Admin").email(email).build();
    }

    private Cliente cliente(Integer id) {
        return Cliente.builder().id(id).nome("Cliente " + id).build();
    }

    /** Evento completo com cliente e administrador para que toResponse() não quebre. */
    private Evento eventoCompleto(Integer id, EventoStatus status, Administrador adm) {
        return Evento.builder()
                .id(id)
                .titulo("Festa")
                .descricao("Descrição")
                .dataInicio(LocalDateTime.now().plusDays(1))
                .dataFim(LocalDateTime.now().plusDays(2))
                .endereco("Rua A")
                .status(status)
                .tipoPagamento("PIX")
                .cliente(cliente(1))
                .administradorCriador(adm)
                .build();
    }

    // ── criar ─────────────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoQuandoDataInicioMaiorQueFim() {
        EventoRequest request = mock(EventoRequest.class);
        LocalDateTime agora = LocalDateTime.now();

        when(request.dataInicio()).thenReturn(agora);
        when(request.dataFim()).thenReturn(agora.minusDays(1));

        assertThrows(
                PreconditionFailedException.class,
                () -> service.criar(request, "admin@email.com")
        );
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    void deveBuscarPorId() {
        Administrador adm = admin(1, "admin@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.CONFIRMADO, adm);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        assertDoesNotThrow(() -> service.buscarPorId(1));
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExiste() {
        when(eventoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(1)
        );
    }

    // ── deletar ───────────────────────────────────────────────────────────────

    @Test
    void deveDeletarEvento() {
        Administrador admin = admin(1, "admin@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.CONFIRMADO, admin);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        service.deletar(1, "admin@email.com");

        verify(eventoRepository).deleteById(1);
    }

    @Test
    void naoDevePermitirDeletarEventoDeOutroAdministrador() {
        Administrador dono = admin(1, "admin@email.com");
        Administrador outro = admin(2, "outro@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.CONFIRMADO, dono);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(administradorRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));

        assertThrows(
                ForbiddenException.class,
                () -> service.deletar(1, "outro@email.com")
        );
    }

    // ── cancelar ──────────────────────────────────────────────────────────────

    @Test
    void deveCancelarEvento() {
        Administrador admin = admin(1, "admin@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.RASCUNHO, admin);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(i -> i.getArgument(0));

        EventoResponse response = service.cancelar(1, "admin@email.com");

        assertEquals(EventoStatus.CANCELADO, response.status());
    }

    @Test
    void naoDeveCancelarEventoJaCancelado() {
        Administrador admin = admin(1, "admin@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.CANCELADO, admin);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        assertThrows(
                ConflictException.class,
                () -> service.cancelar(1, "admin@email.com")
        );
    }

    @Test
    void naoDeveCancelarEventoFinalizado() {
        Administrador admin = admin(1, "admin@email.com");
        Evento evento = eventoCompleto(1, EventoStatus.FINALIZADO, admin);

        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(administradorRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));

        assertThrows(
                BusinessException.class,
                () -> service.cancelar(1, "admin@email.com")
        );
    }
}