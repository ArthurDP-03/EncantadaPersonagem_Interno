package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Cliente;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.ClienteRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteRequest;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveListarClientes() {

        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Maria")
                .telefone("99999999")
                .email("maria@email.com")
                .build();

        when(clienteRepository.findAll())
                .thenReturn(List.of(cliente));

        List<ClienteResponse> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).nome());
    }

    @Test
    void deveCriarCliente() {

        ClienteRequest request = mock(ClienteRequest.class);

        when(request.nome()).thenReturn("Maria");
        when(request.telefone()).thenReturn("99999999");
        when(request.email()).thenReturn("maria@email.com");

        Cliente salvo = Cliente.builder()
                .id(1)
                .nome("Maria")
                .telefone("99999999")
                .email("maria@email.com")
                .build();

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(salvo);

        ClienteResponse response = service.criar(request);

        assertEquals(1, response.id());
        assertEquals("Maria", response.nome());
    }

    @Test
    void deveBuscarPorId() {

        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Maria")
                .build();

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        ClienteResponse response = service.buscarPorId(1);

        assertEquals(1, response.id());
    }

    @Test
    void deveBuscarEntidade() {

        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Maria")
                .build();

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarEntidade(1);

        assertEquals(1, resultado.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarClienteInexistente() {

        when(clienteRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(1)
        );
    }

    @Test
    void deveAtualizarCliente() {

        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Antigo")
                .build();

        ClienteRequest request = mock(ClienteRequest.class);

        when(request.nome()).thenReturn("Novo");
        when(request.telefone()).thenReturn("88888888");
        when(request.email()).thenReturn("novo@email.com");

        when(clienteRepository.findById(1))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClienteResponse response =
                service.atualizar(1, request);

        assertEquals("Novo", response.nome());
        assertEquals("novo@email.com", response.email());
    }

    @Test
    void deveDeletarCliente() {

        when(clienteRepository.existsById(1))
                .thenReturn(true);

        when(eventoRepository.existsByClienteId(1))
                .thenReturn(false);

        service.deletar(1);

        verify(clienteRepository).deleteById(1);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {

        when(clienteRepository.existsById(1))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deletar(1)
        );
    }

    @Test
    void deveLancarExcecaoQuandoClientePossuiEventos() {

        when(clienteRepository.existsById(1))
                .thenReturn(true);

        when(eventoRepository.existsByClienteId(1))
                .thenReturn(true);

        assertThrows(
                ForbiddenException.class,
                () -> service.deletar(1)
        );
    }
}