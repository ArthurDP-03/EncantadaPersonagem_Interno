package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonagemRepository extends JpaRepository<Personagem, Integer> {
}
