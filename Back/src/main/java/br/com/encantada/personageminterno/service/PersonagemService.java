package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Personagem;
import br.com.encantada.personageminterno.repository.PersonagemRepository;
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
}
