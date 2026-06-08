package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import br.com.encantada.personageminterno.domain.enums.PersonagemItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonagemItemRepository extends JpaRepository<PersonagemItem, Integer> {

    boolean existsByCodigo(String codigo);

    boolean existsByPersonagemIdAndStatus(Integer personagemId, PersonagemItemStatus status);

    long countByPersonagemIdAndStatus(Integer personagemId, PersonagemItemStatus status);
}
