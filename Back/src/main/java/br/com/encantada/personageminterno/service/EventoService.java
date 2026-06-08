package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Evento;
import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.PreconditionFailedException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.repository.PersonagemItemRepository;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.AdicionarPersonagemRequest;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ClienteService clienteService;
    private final AdministradorRepository administradorRepository;
    private final PersonagemRepository personagemRepository;
    private final PersonagemItemRepository personagemItemRepository;
    private final EventoPersonagemRepository eventoPersonagemRepository;

    public EventoService(
            EventoRepository eventoRepository,
            ClienteService clienteService,
            AdministradorRepository administradorRepository,
            PersonagemRepository personagemRepository,
            PersonagemItemRepository personagemItemRepository,
            EventoPersonagemRepository eventoPersonagemRepository) {
        this.eventoRepository = eventoRepository;
        this.clienteService = clienteService;
        this.administradorRepository = administradorRepository;
        this.personagemRepository = personagemRepository;
        this.personagemItemRepository = personagemItemRepository;
        this.eventoPersonagemRepository = eventoPersonagemRepository;
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
            throw new PreconditionFailedException("A data de início deve ser anterior à data de fim");
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

    @Transactional
    public EventoPersonagemResponse adicionarPersonagem(int eventoId, AdicionarPersonagemRequest req, String adminEmail) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + eventoId));

        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!evento.getAdministradorCriador().getId().equals(admin.getId())) {
            throw new ForbiddenException("Você não tem permissão para modificar este evento");
        }
        if (evento.getStatus() == EventoStatus.CANCELADO || evento.getStatus() == EventoStatus.FINALIZADO) {
            throw new BusinessException("Não é possível adicionar personagens em evento " + evento.getStatus());
        }

        Personagem personagem = personagemRepository.findById(req.personagemId())
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado com id: " + req.personagemId()));

        if (eventoPersonagemRepository.existsByEventoIdAndPersonagemId(eventoId, req.personagemId())) {
            throw new ConflictException("Personagem já está vinculado a este evento");
        }

        long disponiveis = personagemItemRepository.countByPersonagemIdAndStatus(
                req.personagemId(), PersonagemItemStatus.DISPONIVEL);
        long vagasFuturas = eventoPersonagemRepository.countVagasFuturas(req.personagemId());
        if (disponiveis - vagasFuturas <= 0) {
            throw new BusinessException("Sem fantasias disponíveis para o personagem: " + personagem.getNome());
        }

        EventoPersonagem ep = EventoPersonagem.builder()
                .evento(evento)
                .personagem(personagem)
                .build();
        EventoPersonagem salvo = eventoPersonagemRepository.save(ep);

        return new EventoPersonagemResponse(
                salvo.getId(),
                evento.getId(),
                evento.getTitulo(),
                personagem.getId(),
                personagem.getNome());
    }

    private EventoResponse toResponse(Evento evento) {
        List<EventoPersonagemResponse> personagens = eventoPersonagemRepository
                .findByEventoId(evento.getId())
                .stream()
                .map(ep -> new EventoPersonagemResponse(
                        ep.getId(),
                        ep.getEvento().getId(),
                        ep.getEvento().getTitulo(),
                        ep.getPersonagem().getId(),
                        ep.getPersonagem().getNome()))
                .toList();

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
                evento.getAdministradorCriador().getNome(),
                personagens);
    }

    @Transactional(readOnly = true)
    public EventoResponse buscarPorId(int id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + id));
        return toResponse(evento);
    }

    @Transactional
    public EventoResponse atualizar(int id, EventoRequest request, String administradorEmail) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + id));

        Administrador adminLogado = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!evento.getAdministradorCriador().getId().equals(adminLogado.getId())) {
            throw new ForbiddenException(
                    "Você não tem permissão para atualizar este evento. Apenas o criador pode modificá-lo");
        }
        if (!request.dataInicio().isBefore(request.dataFim())) {
            throw new PreconditionFailedException("A data de início deve ser anterior à data de fim");
        }

        evento.setTitulo(request.titulo());
        evento.setDescricao(request.descricao());
        evento.setDataInicio(request.dataInicio());
        evento.setDataFim(request.dataFim());
        evento.setEndereco(request.endereco());
        evento.setStatus(request.status() == null ? EventoStatus.RASCUNHO : request.status());
        evento.setTipoPagamento(request.tipoPagamento());
        evento.setValorTotal(request.valorTotal());
        evento.setCliente(clienteService.buscarEntidade(request.clienteId()));

        return toResponse(eventoRepository.save(evento));
    }
    

    @Transactional
    public void deletar(int id, String administradorEmail) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + id));
                
        Administrador adminLogado = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!evento.getAdministradorCriador().getId().equals(adminLogado.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar este evento");
        }

        eventoRepository.deleteById(id);
    }

    /**
     * Saída 3: cancelar o evento inteiro quando não há como suprir os personagens.
     * Não exclui o registro: apenas muda o status para CANCELADO.
     */
    @Transactional
    public EventoResponse cancelar(int id, String administradorEmail) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + id));

        Administrador adminLogado = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!evento.getAdministradorCriador().getId().equals(adminLogado.getId())) {
            throw new ForbiddenException("Você não tem permissão para cancelar este evento");
        }
        if (evento.getStatus() == EventoStatus.CANCELADO) {
            throw new ConflictException("Evento já está cancelado");
        }
        if (evento.getStatus() == EventoStatus.FINALIZADO) {
            throw new BusinessException("Evento finalizado não pode ser cancelado");
        }

        evento.setStatus(EventoStatus.CANCELADO);
        return toResponse(eventoRepository.save(evento));
    }
}
