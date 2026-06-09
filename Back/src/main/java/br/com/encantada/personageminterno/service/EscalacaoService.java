package br.com.encantada.personageminterno.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.entity.Escalacao;
import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.domain.enums.EscalacaoStatus;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.ConviteRepository;
import br.com.encantada.personageminterno.repository.EscalacaoRepository;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.PersonagemItemRepository;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoCreateRequest;
import br.com.encantada.personageminterno.web.dto.escalacao.EscalacaoResponse;

@Service
public class EscalacaoService {

    private final EscalacaoRepository escalacaoRepository;
    private final EventoPersonagemRepository epRepository;
    private final ConviteRepository conviteRepository;
    private final PersonagemItemRepository personagemItemRepository;
    private final AdministradorRepository administradorRepository;

    public EscalacaoService(
            EscalacaoRepository escalacaoRepository,
            EventoPersonagemRepository epRepository,
            ConviteRepository conviteRepository,
            PersonagemItemRepository personagemItemRepository,
            AdministradorRepository administradorRepository) {
        this.escalacaoRepository = escalacaoRepository;
        this.epRepository = epRepository;
        this.conviteRepository = conviteRepository;
        this.personagemItemRepository = personagemItemRepository;
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public EscalacaoResponse escolherAtorFinal(EscalacaoCreateRequest req, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        EventoPersonagem ep = epRepository.findById(req.eventoPersonagemId())
                .orElseThrow(() -> new ResourceNotFoundException("EventoPersonagem não encontrado"));

        if (escalacaoRepository.existsByEventoPersonagemId(ep.getId())) {
            throw new ConflictException("EventoPersonagem já possui escalação");
        }

        Convite convite = conviteRepository
                .findByEventoPersonagemIdAndAtorId(ep.getId(), req.atorId())
                .orElseThrow(() -> new BusinessException("Ator não foi convidado para este personagem"));

        if (convite.getStatus() != ConviteStatus.ACEITO) {
            throw new BusinessException("Ator não aceitou o convite");
        }

        PersonagemItem item = personagemItemRepository.findById(req.personagemItemId())
                .orElseThrow(() -> new ResourceNotFoundException("PersonagemItem não encontrado"));

        if (!item.getPersonagem().getId().equals(ep.getPersonagemItem().getPersonagem().getId())) {
            throw new BusinessException("O item escolhido não pertence ao personagem do evento");
        }
        if (item.getStatus() != PersonagemItemStatus.DISPONIVEL) {
            throw new BusinessException("PersonagemItem indisponível");
        }

        item.setStatus(PersonagemItemStatus.EM_USO);
        personagemItemRepository.save(item);

        Escalacao esc = Escalacao.builder()
                .eventoPersonagem(ep)
                .ator(convite.getAtor())
                .personagemItem(item)
                .administrador(admin)
                .dataEscolha(LocalDateTime.now())
                .status(EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR)
                .build();

        return toResponse(escalacaoRepository.save(esc));
    }

    @Transactional
    public EscalacaoResponse confirmarPresenca(int escalacaoId, String atorEmail) {
        Escalacao e = escalacaoRepository.findById(escalacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Escalação não encontrada"));

        if (!e.getAtor().getEmail().equalsIgnoreCase(atorEmail)) {
            throw new ForbiddenException("Apenas o ator escalado pode confirmar a presença");
        }
        if (e.getStatus() != EscalacaoStatus.PENDENTE_CONFIRMACAO_ATOR) {
            throw new BusinessException("Escalação não está aguardando confirmação");
        }

        e.setStatus(EscalacaoStatus.CONFIRMADA);
        e.setDataConfirmacaoAtor(LocalDateTime.now());
        return toResponse(escalacaoRepository.save(e));
    }

    @Transactional
    public void cancelar(int escalacaoId, String adminEmail) {
        Escalacao e = escalacaoRepository.findById(escalacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Escalação não encontrada"));

        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (!e.getAdministrador().getId().equals(admin.getId())) {
            throw new ForbiddenException("Você não tem permissão para cancelar esta escalação");
        }
        if (e.getStatus() == EscalacaoStatus.CANCELADA) {
            throw new BusinessException("Escalação já está cancelada");
        }

        PersonagemItem item = e.getPersonagemItem();
        if (item.getStatus() == PersonagemItemStatus.EM_USO) {
            item.setStatus(PersonagemItemStatus.DISPONIVEL);
            personagemItemRepository.save(item);
        }

        e.setStatus(EscalacaoStatus.CANCELADA);
        escalacaoRepository.save(e);
    }

    @Transactional(readOnly = true)
    public EscalacaoResponse buscarPorId(int id) {
        Escalacao e = escalacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escalação não encontrada"));
        return toResponse(e);
    }

    private EscalacaoResponse toResponse(Escalacao e) {
        EventoPersonagem ep = e.getEventoPersonagem();
        PersonagemItem item = e.getPersonagemItem();
        return new EscalacaoResponse(
                e.getId(),
                ep.getId(),
                ep.getEvento().getId(),
                ep.getEvento().getTitulo(),
                ep.getPersonagemItem().getPersonagem().getId(),
                ep.getPersonagemItem().getPersonagem().getNome(),
                e.getAtor().getId(),
                e.getAtor().getNome(),
                item.getId(),
                item.getCodigo(),
                e.getAdministrador().getId(),
                e.getStatus(),
                e.getDataEscolha(),
                e.getDataConfirmacaoAtor());
    }
}
