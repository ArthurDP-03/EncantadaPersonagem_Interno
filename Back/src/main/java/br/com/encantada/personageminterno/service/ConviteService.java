package br.com.encantada.personageminterno.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.domain.enums.EscalacaoStatus;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.PreconditionFailedException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.repository.ConviteRepository;
import br.com.encantada.personageminterno.repository.EscalacaoRepository;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.PersonagemItemRepository;
import br.com.encantada.personageminterno.web.dto.convite.ConviteAtorItemRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteCreateRequest;
import br.com.encantada.personageminterno.web.dto.convite.ConviteResponse;
import br.com.encantada.personageminterno.web.dto.convite.ConviteRespostaRequest;

@Service
public class ConviteService {

    private final ConviteRepository conviteRepository;
    private final EventoPersonagemRepository epRepository;
    private final AtorRepository atorRepository;
    private final AdministradorRepository administradorRepository;
    private final PersonagemItemRepository personagemItemRepository;
    private final EscalacaoRepository escalacaoRepository;

    public ConviteService(
            ConviteRepository conviteRepository,
            EventoPersonagemRepository epRepository,
            AtorRepository atorRepository,
            AdministradorRepository administradorRepository,
            PersonagemItemRepository personagemItemRepository,
            EscalacaoRepository escalacaoRepository) {
        this.conviteRepository = conviteRepository;
        this.epRepository = epRepository;
        this.atorRepository = atorRepository;
        this.administradorRepository = administradorRepository;
        this.personagemItemRepository = personagemItemRepository;
        this.escalacaoRepository = escalacaoRepository;
    }

    @Transactional
    public List<ConviteResponse> enviarConvites(ConviteCreateRequest req, String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin não encontrado"));

        EventoPersonagem ep = epRepository.findById(req.eventoPersonagemId())
                .orElseThrow(() -> new ResourceNotFoundException("EventoPersonagem não encontrado"));

        // não permitir convites em evento cancelado/finalizado
        EventoStatus st = ep.getEvento().getStatus();
        if (st == EventoStatus.CANCELADO || st == EventoStatus.FINALIZADO) {
            throw new BusinessException("Não é possível enviar convites em evento " + st);
        }

        List<Convite> criados = new ArrayList<>();
        Set<Integer> atoresNoPayload = new HashSet<>();
        for (ConviteAtorItemRequest par : req.convites()) {
            if (!atoresNoPayload.add(par.atorId())) {
                throw new BusinessException("Ator " + par.atorId() + " foi informado mais de uma vez");
            }
            if (conviteRepository.findByEventoPersonagemIdAndAtorId(ep.getId(), par.atorId()).isPresent()) {
                continue;
            }
            Ator ator = atorRepository.findById(par.atorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ator " + par.atorId() + " não encontrado"));

            if (conviteRepository.existsAtorComConviteAtivoEmOutroPersonagemDoEvento(
                    ep.getEvento().getId(), ator.getId(), ep.getId())
                    || escalacaoRepository.existsAtorEscaladoEmOutroPersonagemDoEvento(
                            ep.getEvento().getId(), ator.getId(), ep.getId())) {
                throw new BusinessException(
                        "Ator " + ator.getNome() + " já está vinculado a outro personagem deste evento");
            }

            PersonagemItem item = personagemItemRepository.findById(par.personagemItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item de personagem " + par.personagemItemId() + " não encontrado"));

            if (!item.getId().equals(ep.getPersonagemItem().getId())) {
                throw new BusinessException("Item " + item.getCodigo() + " não está vinculado a este evento-personagem");
            }
            if (item.getStatus() != PersonagemItemStatus.EM_USO) {
                throw new BusinessException("Item " + item.getCodigo() + " não está em uso neste evento");
            }

            Convite c = Convite.builder()
                    .eventoPersonagem(ep)
                    .ator(ator)
                    .administrador(admin)
                    .personagemItem(item)
                    .status(ConviteStatus.PENDENTE)
                    .dataEnvio(LocalDateTime.now())
                    .dataExpiracao(calcularExpiracao(ep.getEvento().getDataInicio()))
                    .build();
            criados.add(conviteRepository.save(c));
        }
        return criados.stream().map(c -> toResponse(c)).toList();
    }

    @Transactional(readOnly = true)
    public List<ConviteResponse> listarMeusConvites(String adminEmail) {
        Administrador admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));
        return conviteRepository.findByAdministradorId(admin.getId()).stream()
                .map(c -> toResponse(c))
                .toList();
    }

    @Transactional
    public ConviteResponse responder(int conviteId, ConviteRespostaRequest req, String atorEmail) {
        Convite c = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado"));

        // autorização: só o ator dono pode responder
        if (!c.getAtor().getEmail().equalsIgnoreCase(atorEmail)) {
            throw new ForbiddenException("Você não pode responder este convite");
        }
        if (c.getStatus() == ConviteStatus.EXPIRADO) {
            throw new BusinessException("Convite expirado, não pode ser respondido");
        }
        if (c.getStatus() != ConviteStatus.PENDENTE) {
            throw new BusinessException("Convite já foi respondido");
        }
        if (req.status() != ConviteStatus.ACEITO && req.status() != ConviteStatus.RECUSADO) {
            throw new PreconditionFailedException("Status inválido para resposta");
        }

        c.setStatus(req.status());
        c.setDataResposta(LocalDateTime.now());
        return toResponse(conviteRepository.save(c));
    }

    @Transactional(readOnly = true)
    public List<ConviteResponse> listarPorEventoPersonagem(int epId) {
        if (!epRepository.existsById(epId)) {
            throw new ResourceNotFoundException("EventoPersonagem não encontrado");
        }
        return conviteRepository.findByEventoPersonagemId(epId).stream()
                .map(c -> toResponse(c))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConviteResponse> listarMeus(String atorEmail) {
        return listarMeus(atorEmail, ConviteStatus.PENDENTE);
    }

    @Transactional(readOnly = true)
    public List<ConviteResponse> listarMeus(String atorEmail, ConviteStatus status) {
        Ator ator = atorRepository.findByEmail(atorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Ator autenticado não encontrado"));

        List<Convite> convites = status == null
                ? conviteRepository.findByAtorId(ator.getId())
                : conviteRepository.findByAtorIdAndStatus(ator.getId(), status);

        return convites.stream()
                .map(c -> toResponse(c))
                .toList();
    }

    @Transactional
    public void cancelar(int conviteId, String adminEmail) {
        Convite c = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado"));

        administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (c.getStatus() != ConviteStatus.PENDENTE) {
            throw new BusinessException("Apenas convites pendentes podem ser cancelados");
        }
        c.setStatus(ConviteStatus.CANCELADO);
        conviteRepository.save(c);
    }

    @Transactional
    public ConviteResponse reativar(int conviteId, String adminEmail) {
        Convite c = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado"));

        administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (c.getStatus() != ConviteStatus.CANCELADO) {
            throw new BusinessException("Apenas convites cancelados podem ser reativados");
        }

        EventoPersonagem ep = c.getEventoPersonagem();
        EventoStatus st = ep.getEvento().getStatus();
        if (st == EventoStatus.CANCELADO || st == EventoStatus.FINALIZADO) {
            throw new BusinessException("Não é possível reativar convites em evento " + st);
        }

        if (escalacaoRepository.existsByEventoPersonagemIdAndStatusNot(
                ep.getId(), EscalacaoStatus.CANCELADA)) {
            throw new BusinessException("Este personagem já possui escalação ativa");
        }

        if (conviteRepository.existsAtorComConviteAtivoEmOutroPersonagemDoEvento(
                ep.getEvento().getId(), c.getAtor().getId(), ep.getId())
                || escalacaoRepository.existsAtorEscaladoEmOutroPersonagemDoEvento(
                        ep.getEvento().getId(), c.getAtor().getId(), ep.getId())) {
            throw new BusinessException(
                    "Ator " + c.getAtor().getNome() + " já está vinculado a outro personagem deste evento");
        }

        c.setStatus(ConviteStatus.PENDENTE);
        c.setDataResposta(null);
        c.setDataEnvio(LocalDateTime.now());
        c.setDataExpiracao(calcularExpiracao(ep.getEvento().getDataInicio()));
        return toResponse(conviteRepository.save(c));
    }

    /** True quando todos convites do EP estão RECUSADO (e existe pelo menos 1). */
    @Transactional(readOnly = true)
    public boolean personagemIndisponivel(int epId) {
        long total = conviteRepository.findByEventoPersonagemId(epId).size();
        long recusados = conviteRepository.countByEventoPersonagemIdAndStatus(epId, ConviteStatus.RECUSADO);
        return total > 0 && total == recusados;
    }

    private ConviteResponse toResponse(Convite c) {
        EventoPersonagem ep = c.getEventoPersonagem();
        return new ConviteResponse(
                c.getId(),
                ep.getId(),
                ep.getEvento().getId(),
                ep.getEvento().getTitulo(),
                ep.getPersonagemItem().getPersonagem().getId(),
                ep.getPersonagemItem().getPersonagem().getNome(),
                c.getPersonagemItem().getId(),
                c.getPersonagemItem().getCodigo(),
                c.getAtor().getId(),
                c.getAtor().getNome(),
                c.getAdministrador().getId(),
                c.getStatus(),
                c.getDataEnvio(),
                c.getDataExpiracao(),
                c.getDataResposta());
    }

    private LocalDateTime calcularExpiracao(LocalDateTime dataInicio) {
        LocalDateTime porPrazo = LocalDateTime.now().plusDays(7);
        LocalDateTime porEvento = dataInicio.minusDays(1);
        return porEvento.isBefore(porPrazo) ? porEvento : porPrazo;
    }
}
