package br.com.encantada.personageminterno.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.ConviteRepository;
import br.com.encantada.personageminterno.repository.EscalacaoRepository;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.EventoPersonagemResponse;
import br.com.encantada.personageminterno.web.dto.eventopersonagem.ReabrirConvitesRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.TrocarPersonagemRequest;

@Service
public class EventoPersonagemService {

    private final EventoPersonagemRepository epRepository;
    private final PersonagemRepository personagemRepository;
    private final ConviteRepository conviteRepository;
    private final EscalacaoRepository escalacaoRepository;
    private final AdministradorRepository administradorRepository;
    private final ConviteService conviteService;

    public EventoPersonagemService(
            EventoPersonagemRepository epRepository,
            PersonagemRepository personagemRepository,
            ConviteRepository conviteRepository,
            EscalacaoRepository escalacaoRepository,
            AdministradorRepository administradorRepository,
            ConviteService conviteService) {
        this.epRepository = epRepository;
        this.personagemRepository = personagemRepository;
        this.conviteRepository = conviteRepository;
        this.escalacaoRepository = escalacaoRepository;
        this.administradorRepository = administradorRepository;
        this.conviteService = conviteService;
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
        if (req.novoPersonagemId().equals(ep.getPersonagem().getId())) {
            throw new BusinessException("O novo personagem deve ser diferente do atual");
        }
        if (epRepository.existsByEventoIdAndPersonagemId(ep.getEvento().getId(), req.novoPersonagemId())) {
            throw new ConflictException("Este personagem já está vinculado ao evento");
        }

        Personagem novo = personagemRepository.findById(req.novoPersonagemId())
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));

        List<Convite> antigos = conviteRepository.findByEventoPersonagemId(ep.getId());
        if (!antigos.isEmpty()) {
            conviteRepository.deleteAll(antigos);
        }

        ep.setPersonagem(novo);
        return toResponse(epRepository.save(ep));
    }

    /**
     * Remove o personagem de um evento, apagando os convites associados.
     * Só é permitido quando não há escalação e o evento ainda é editável.
     */
    @Transactional
    public void removerPersonagem(int eventoId, int epId, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        EventoPersonagem ep = epRepository.findById(epId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento-personagem não encontrado"));

        if (!ep.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Evento-personagem não pertence ao evento informado");
        }

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
                new ConviteCreateRequest(ep.getId(), req.atoresIds()),
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
        return new EventoPersonagemResponse(
                ep.getId(),
                ep.getEvento().getId(),
                ep.getEvento().getTitulo(),
                ep.getPersonagem().getId(),
                ep.getPersonagem().getNome());
    }
}
