package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Convite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConviteRepository extends JpaRepository<Convite, Integer> {

    Optional<Convite> findByEventoPersonagemIdAndAtorId(Integer eventoPersonagemId, Integer atorId);
}
