package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Ator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtorRepository extends JpaRepository<Ator, Integer> {

    boolean existsByEmail(String email);
}
