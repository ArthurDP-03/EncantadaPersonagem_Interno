package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConviteRepository extends JpaRepository<Convite, Integer> {
    List<Convite> findByEventoPersonagemId(Integer eventoPersonagemId);

    List<Convite> findByEventoPersonagemIdAndStatus(Integer epId, ConviteStatus status);

    List<Convite> findByAtorIdAndStatus(Integer atorId, ConviteStatus status);

    List<Convite> findByAdministradorId(Integer administradorId);

    long countByEventoPersonagemIdAndStatus(Integer epId, ConviteStatus status);

    Optional<Convite> findByEventoPersonagemIdAndAtorId(Integer eventoPersonagemId, Integer atorId);
}
