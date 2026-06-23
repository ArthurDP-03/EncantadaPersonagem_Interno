package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoPersonagemRepository extends JpaRepository<EventoPersonagem, Integer> {

    boolean existsByEventoIdAndPersonagemItemId(Integer eventoId, Integer personagemItemId);

    boolean existsByPersonagemItemId(Integer personagemItemId);

    List<EventoPersonagem> findByEventoId(Integer eventoId);

    @Query("SELECT COUNT(ep) FROM EventoPersonagem ep " +
           "WHERE ep.personagemItem.id = :personagemItemId " +
           "AND ep.evento.dataInicio > CURRENT_TIMESTAMP " +
           "AND ep.evento.status NOT IN " +
           "(br.com.encantada.personageminterno.domain.enums.EventoStatus.CANCELADO, " +
           "br.com.encantada.personageminterno.domain.enums.EventoStatus.FINALIZADO)")
    long countVagasFuturas(@Param("personagemItemId") Integer personagemItemId);
}
