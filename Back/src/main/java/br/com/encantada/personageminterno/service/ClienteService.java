package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Cliente;
import br.com.encantada.personageminterno.repository.ClienteRepository;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteRequest;
import br.com.encantada.personageminterno.web.dto.cliente.ClienteResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
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

    public Cliente buscarEntidade(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new br.com.encantada.personageminterno.exception.ResourceNotFoundException("Cliente nao encontrado"));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail());
    }
}
