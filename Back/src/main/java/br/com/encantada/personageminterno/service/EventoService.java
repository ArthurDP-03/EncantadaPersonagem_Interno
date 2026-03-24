package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Evento;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ClienteService clienteService;
    private final AdministradorRepository administradorRepository;

    public EventoService(
            EventoRepository eventoRepository,
            ClienteService clienteService,
            AdministradorRepository administradorRepository
    ) {
        this.eventoRepository = eventoRepository;
        this.clienteService = clienteService;
        this.administradorRepository = administradorRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listar() {
        return eventoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EventoResponse criar(EventoRequest request, String administradorEmail) {
        if (!request.dataInicio().isBefore(request.dataFim())) {
            throw new BusinessException("A data de inicio deve ser anterior a data de fim");
        }

        Administrador administrador = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado nao encontrado"));

        Evento evento = Evento.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .endereco(request.endereco())
                .status(request.status() == null ? EventoStatus.RASCUNHO : request.status())
                .tipoPagamento(request.tipoPagamento())
                .valorTotal(request.valorTotal())
                .cliente(clienteService.buscarEntidade(request.clienteId()))
                .administradorCriador(administrador)
                .build();

        return toResponse(eventoRepository.save(evento));
    }

    private EventoResponse toResponse(Evento evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getEndereco(),
                evento.getStatus(),
                evento.getTipoPagamento(),
                evento.getValorTotal(),
                evento.getCliente().getId(),
                evento.getCliente().getNome(),
                evento.getAdministradorCriador().getId(),
                evento.getAdministradorCriador().getNome()
        );
    }
}
