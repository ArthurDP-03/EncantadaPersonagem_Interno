package br.com.encantada.personageminterno.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.entity.Evento;
import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.ConviteRepository;
import br.com.encantada.personageminterno.repository.EscalacaoRepository;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.EventoRepository;
import br.com.encantada.personageminterno.repository.PersonagemItemRepository;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.AdicionarPersonagemRequest;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.ReabrirConvitesRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.TrocarPersonagemRequest;

@Service
public class EventoPersonagemService {

    private final EventoPersonagemRepository epRepository;
    private final PersonagemItemRepository personagemItemRepository;
    private final ConviteRepository conviteRepository;
    private final EscalacaoRepository escalacaoRepository;
    private final AdministradorRepository administradorRepository;
    private final ConviteService conviteService;
    private final EventoRepository eventoRepository;

    public EventoPersonagemService(
            EventoPersonagemRepository epRepository,
            PersonagemItemRepository personagemItemRepository,
            ConviteRepository conviteRepository,
            EscalacaoRepository escalacaoRepository,
            AdministradorRepository administradorRepository,
            ConviteService conviteService,
            EventoRepository eventoRepository) {
        this.epRepository = epRepository;
        this.personagemItemRepository = personagemItemRepository;
        this.conviteRepository = conviteRepository;
        this.escalacaoRepository = escalacaoRepository;
        this.administradorRepository = administradorRepository;
        this.conviteService = conviteService;
        this.eventoRepository = eventoRepository;
    }

    /**
     * Saída 1: trocar o personagem desejado no evento quando todos os atores recusaram.
     * Limpa os convites antigos e troca o personagem do EventoPersonagem.
     */
    @Transactional
    public EventoPersonagemResponse trocarPersonagem(int epId, TrocarPersonagemRequest req, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        EventoPersonagem ep = epRepository.findById(epId)
                .orElseThrow(() -> new ResourceNotFoundException("EventoPersonagem não encontrado"));

        validarPermissaoAdmin(ep, admin);
        validarEventoEditavel(ep);

        if (escalacaoRepository.existsByEventoPersonagemId(ep.getId())) {
            throw new ConflictException("Não é possível trocar o personagem: já existe escalação para este EventoPersonagem");
        }
        if (!conviteService.personagemIndisponivel(ep.getId())) {
            throw new BusinessException("Ainda há convites pendentes ou aceitos para este personagem");
        }
        if (req.novoPersonagemItemId().equals(ep.getPersonagemItem().getId())) {
            throw new BusinessException("O novo item de personagem deve ser diferente do atual");
        }
        if (epRepository.existsByEventoIdAndPersonagemItemId(ep.getEvento().getId(), req.novoPersonagemItemId())) {
            throw new ConflictException("Este item de personagem já está vinculado ao evento");
        }

        PersonagemItem novoItem = personagemItemRepository.findById(req.novoPersonagemItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item de personagem não encontrado"));
        if (novoItem.getStatus() != PersonagemItemStatus.DISPONIVEL) {
            throw new BusinessException("O item de personagem não está disponível: " + novoItem.getCodigo());
        }

        List<Convite> antigos = conviteRepository.findByEventoPersonagemId(ep.getId());
        if (!antigos.isEmpty()) {
            conviteRepository.deleteAll(antigos);
        }

        ep.setPersonagemItem(novoItem);
        return toResponse(epRepository.save(ep));
    }

    /**
     * Remove o personagem de um evento, apagando os convites associados.
     * Só é permitido quando não há escalação e o evento ainda é editável.
     */
    @Transactional
    public void removerPersonagem(int epId, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        EventoPersonagem ep = epRepository.findById(epId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento-personagem não encontrado"));

        validarPermissaoAdmin(ep, admin);
        validarEventoEditavel(ep);

        if (escalacaoRepository.existsByEventoPersonagemId(ep.getId())) {
            throw new ConflictException("Não é possível remover: já existe escalação para este evento-personagem");
        }

        List<Convite> convites = conviteRepository.findByEventoPersonagemId(ep.getId());
        if (!convites.isEmpty()) {
            conviteRepository.deleteAll(convites);
        }
        epRepository.delete(ep);
    }

    @Transactional
    public EventoPersonagemResponse adicionarPersonagem(AdicionarPersonagemRequest req, String adminEmail) {
        Evento evento = eventoRepository.findById(req.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com id: " + req.eventoId()));

        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!evento.getAdministradorCriador().getId().equals(admin.getId())) {
            throw new ForbiddenException("Você não tem permissão para modificar este evento");
        }
        if (evento.getStatus() == EventoStatus.CANCELADO || evento.getStatus() == EventoStatus.FINALIZADO) {
            throw new BusinessException("Não é possível adicionar personagens em evento " + evento.getStatus());
        }

        PersonagemItem personagemItem = personagemItemRepository.findById(req.personagemItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item de personagem não encontrado com id: " + req.personagemItemId()));

        if (personagemItem.getStatus() != PersonagemItemStatus.DISPONIVEL) {
            throw new BusinessException("O item de personagem não está disponível: " + personagemItem.getCodigo());
        }

        if (epRepository.existsByEventoIdAndPersonagemItemId(req.eventoId(), req.personagemItemId())) {
            throw new ConflictException("Este item de personagem já está vinculado a este evento");
        }

        long vagasFuturas = epRepository.countVagasFuturas(req.personagemItemId());
        if (vagasFuturas > 0) {
            throw new BusinessException("Este item de personagem já está reservado para outro evento futuro");
        }

        EventoPersonagem ep = EventoPersonagem.builder()
                .evento(evento)
                .personagemItem(personagemItem)
                .build();
        EventoPersonagem salvo = epRepository.save(ep);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<EventoPersonagemResponse> listarPersonagens(int eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResourceNotFoundException("Evento não encontrado com id: " + eventoId);
        }
        return epRepository.findByEventoId(eventoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Saída 2: manter o personagem, mas enviar convites para um novo conjunto de atores.
     * Antes de reabrir, remove os convites recusados antigos.
     */
    @Transactional
    public List<ConviteResponse> reabrirConvites(int epId, ReabrirConvitesRequest req, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        EventoPersonagem ep = epRepository.findById(epId)
                .orElseThrow(() -> new ResourceNotFoundException("EventoPersonagem não encontrado"));

        validarPermissaoAdmin(ep, admin);
        validarEventoEditavel(ep);

        if (escalacaoRepository.existsByEventoPersonagemId(ep.getId())) {
            throw new ConflictException("Já existe escalação para este EventoPersonagem; convites não podem ser reabertos");
        }
        if (!conviteService.personagemIndisponivel(ep.getId())) {
            throw new BusinessException("Ainda há convites pendentes ou aceitos para este personagem");
        }

        List<Convite> recusados = conviteRepository.findByEventoPersonagemId(ep.getId());
        if (!recusados.isEmpty()) {
            conviteRepository.deleteAll(recusados);
        }

        return conviteService.enviarConvites(
                new ConviteCreateRequest(ep.getId(), req.convites()),
                adminEmail);
    }

    private void validarPermissaoAdmin(EventoPersonagem ep, Administrador admin) {
        Integer criadorId = ep.getEvento().getAdministradorCriador().getId();
        if (!criadorId.equals(admin.getId())) {
            throw new ForbiddenException("Você não tem permissão para gerenciar este evento");
        }
    }

    private void validarEventoEditavel(EventoPersonagem ep) {
        EventoStatus st = ep.getEvento().getStatus();
        if (st == EventoStatus.CANCELADO || st == EventoStatus.FINALIZADO) {
            throw new BusinessException("Evento " + st + " não pode ser modificado");
        }
    }

    private EventoPersonagemResponse toResponse(EventoPersonagem ep) {
        PersonagemItem item = ep.getPersonagemItem();
        return new EventoPersonagemResponse(
                ep.getId(),
                ep.getEvento().getId(),
                ep.getEvento().getTitulo(),
                item.getId(),
                item.getCodigo(),
                item.getPersonagem().getNome());
    }
}
