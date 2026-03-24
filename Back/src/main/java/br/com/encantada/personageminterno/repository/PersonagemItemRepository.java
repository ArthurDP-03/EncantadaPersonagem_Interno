package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.PersonagemItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonagemItemRepository extends JpaRepository<PersonagemItem, Integer> {

    boolean existsByCodigo(String codigo);
}
