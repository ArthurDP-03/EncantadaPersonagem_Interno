package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByEmail(String email);

    boolean existsByEmail(String email);
}
