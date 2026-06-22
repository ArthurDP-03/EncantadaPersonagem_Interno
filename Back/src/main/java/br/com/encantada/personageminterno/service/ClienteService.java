package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Cliente;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.ClienteRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteRequest;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EventoRepository eventoRepository;

    public ClienteService(ClienteRepository clienteRepository, EventoRepository eventoRepository) {
        this.clienteRepository = clienteRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .telefone(request.telefone())
                .email(request.email())
                .build();

        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public Cliente buscarEntidade(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(int id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));
        return toResponse(cliente);
    }

    @Transactional
    public ClienteResponse atualizar(int id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());

        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(int id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente nao encontrado");
        }

        if (eventoRepository.existsByClienteId(id)) {
            throw new ForbiddenException(
                    "Nao e possivel deletar cliente com eventos cadastrados. Remova os eventos primeiro");
        }

        clienteRepository.deleteById(id);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail());
    }
}
