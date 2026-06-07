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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    void deveBuscarPorId() {

        Evento evento = mock(Evento.class);

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        assertDoesNotThrow(() ->
                service.buscarPorId(1)
        );
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExiste() {

        when(eventoRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(1)
        );
    }

    @Test
    void deveDeletarEvento() {

        Administrador admin = Administrador.builder()
                .id(1)
                .email("admin@email.com")
                .build();

        Evento evento = Evento.builder()
                .id(1)
                .administradorCriador(admin)
                .build();

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        when(administradorRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(admin));

        service.deletar(1, "admin@email.com");

        verify(eventoRepository).deleteById(1);
    }

    @Test
    void naoDevePermitirDeletarEventoDeOutroAdministrador() {

        Administrador dono = Administrador.builder()
                .id(1)
                .build();

        Administrador outro = Administrador.builder()
                .id(2)
                .email("outro@email.com")
                .build();

        Evento evento = Evento.builder()
                .id(1)
                .administradorCriador(dono)
                .build();

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        when(administradorRepository.findByEmail("outro@email.com"))
                .thenReturn(Optional.of(outro));

        assertThrows(
                ForbiddenException.class,
                () -> service.deletar(1, "outro@email.com")
        );
    }

    @Test
    void deveCancelarEvento() {

        Administrador admin = Administrador.builder()
                .id(1)
                .email("admin@email.com")
                .build();

        Evento evento = Evento.builder()
                .id(1)
                .status(EventoStatus.RASCUNHO)
                .administradorCriador(admin)
                .build();

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        when(administradorRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(admin));

        when(eventoRepository.save(any(Evento.class)))
                .thenAnswer(i -> i.getArgument(0));

        EventoResponse response =
                service.cancelar(1, "admin@email.com");

        assertEquals(
                EventoStatus.CANCELADO,
                response.status()
        );
    }

    @Test
    void naoDeveCancelarEventoJaCancelado() {

        Administrador admin = Administrador.builder()
                .id(1)
                .email("admin@email.com")
                .build();

        Evento evento = Evento.builder()
                .status(EventoStatus.CANCELADO)
                .administradorCriador(admin)
                .build();

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        when(administradorRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(admin));

        assertThrows(
                ConflictException.class,
                () -> service.cancelar(1, "admin@email.com")
        );
    }

    @Test
    void naoDeveCancelarEventoFinalizado() {

        Administrador admin = Administrador.builder()
                .id(1)
                .email("admin@email.com")
                .build();

        Evento evento = Evento.builder()
                .status(EventoStatus.FINALIZADO)
                .administradorCriador(admin)
                .build();

        when(eventoRepository.findById(1))
                .thenReturn(Optional.of(evento));

        when(administradorRepository.findByEmail("admin@email.com"))
                .thenReturn(Optional.of(admin));

        assertThrows(
                BusinessException.class,
                () -> service.cancelar(1, "admin@email.com")
        );
    }
}