package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Evento;
import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.domain.enums.EventoStatus;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
import br.com.encantada.personageminterno.web.dto.evento.EventoRequest;
import br.com.encantada.personageminterno.web.dto.evento.EventoResponse;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonagemService {

    private final PersonagemRepository personagemRepository;

    public PersonagemService(PersonagemRepository personagemRepository) {
        this.personagemRepository = personagemRepository;
    }

    @Transactional(readOnly = true)
    public List<PersonagemResponse> listar() {
        return personagemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PersonagemResponse criar(PersonagemRequest request) {
        Personagem personagem = Personagem.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .foto(request.foto())
                .build();

        return toResponse(personagemRepository.save(personagem));
    }

    private PersonagemResponse toResponse(Personagem personagem) {
        return new PersonagemResponse(personagem.getId(), personagem.getNome(), personagem.getDescricao(), personagem.getFoto());
    }
    @Transactional(readOnly = true)
public PersonagemResponse buscarPorId(int id) {
    Personagem personagem = personagemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado com id: " + id));
    return toResponse(personagem);
}

@Transactional
public PersonagemResponse atualizar(int id, PersonagemRequest request) {
    Personagem personagem = personagemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado com id: " + id));
    
    personagem.setNome(request.nome());
    personagem.setDescricao(request.descricao());
    personagem.setFoto(request.foto());
    
    return toResponse(personagemRepository.save(personagem));
}

@Transactional
public void deletar(int id) {
    if (!personagemRepository.existsById(id)) {
        throw new ResourceNotFoundException("Personagem não encontrado com id: " + id);
    }
    personagemRepository.deleteById(id);
}
}
