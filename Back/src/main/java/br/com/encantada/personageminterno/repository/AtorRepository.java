package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Ator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtorRepository extends JpaRepository<Ator, Integer> {

    Optional<Ator> findByEmail(String email);

    boolean existsByEmail(String email);
}
