package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.EventoPersonagemRepository;
import br.com.encantada.personageminterno.repository.PersonagemItemRepository;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemRequest;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemResponse;
import br.com.encantada.personageminterno.web.dto.personagemitem.PersonagemItemStatusRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonagemItemService {

    private final PersonagemItemRepository personagemItemRepository;
    private final PersonagemRepository personagemRepository;
    private final EventoPersonagemRepository eventoPersonagemRepository;

    public PersonagemItemService(PersonagemItemRepository personagemItemRepository,
                                 PersonagemRepository personagemRepository,
                                 EventoPersonagemRepository eventoPersonagemRepository) {
        this.personagemItemRepository = personagemItemRepository;
        this.personagemRepository = personagemRepository;
        this.eventoPersonagemRepository = eventoPersonagemRepository;
    }

    @Transactional(readOnly = true)
    public List<PersonagemItemResponse> listar() {
        return personagemItemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonagemItemResponse buscarPorId(Integer id) {
        PersonagemItem item = personagemItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de personagem não encontrado com id: " + id));
        return toResponse(item);
    }

    @Transactional
    public PersonagemItemResponse criar(PersonagemItemRequest request) {
        Personagem personagem = personagemRepository.findById(request.idPersonagem())
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado com id: " + request.idPersonagem()));

        String codigo = gerarCodigo(personagem);

        PersonagemItem item = PersonagemItem.builder()
                .personagem(personagem)
                .codigo(codigo)
                .status(PersonagemItemStatus.DISPONIVEL)
                .build();

        return toResponse(personagemItemRepository.save(item));
    }

    private String gerarCodigo(Personagem personagem) {
        String prefixo = personagem.getNome()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(10, personagem.getNome().toUpperCase().replaceAll("[^A-Z0-9]", "").length()));
        long total = personagemItemRepository.countByPersonagemId(personagem.getId());
        String candidato;
        do {
            total++;
            candidato = prefixo + "-" + String.format("%03d", total);
        } while (personagemItemRepository.existsByCodigo(candidato));
        return candidato;
    }

    @Transactional
    public PersonagemItemResponse atualizarStatus(Integer id, PersonagemItemStatusRequest request) {
        PersonagemItem item = personagemItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de personagem não encontrado com id: " + id));

        validarMudancaStatus(item, request.status());
        item.setStatus(request.status());
        return toResponse(personagemItemRepository.save(item));
    }

    @Transactional
    public void deletar(Integer id) {
        if (!personagemItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item de personagem não encontrado com id: " + id);
        }
        personagemItemRepository.deleteById(id);
    }

    private PersonagemItemResponse toResponse(PersonagemItem item) {
        return new PersonagemItemResponse(
                item.getId(),
                item.getPersonagem().getId(),
                item.getPersonagem().getNome(),
                item.getCodigo(),
                item.getStatus()
        );
    }

    private void validarMudancaStatus(PersonagemItem item, PersonagemItemStatus novoStatus) {
        boolean vinculadoAoEvento = eventoPersonagemRepository.existsByPersonagemItemId(item.getId());

        if (vinculadoAoEvento && novoStatus != PersonagemItemStatus.EM_USO) {
            throw new BusinessException("Item vinculado a evento deve permanecer EM_USO");
        }
        if (!vinculadoAoEvento && novoStatus == PersonagemItemStatus.EM_USO) {
            throw new BusinessException("Item sem vínculo com evento não pode ser marcado como EM_USO");
        }
    }
}
