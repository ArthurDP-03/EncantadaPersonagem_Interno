package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoPersonagemRepository extends JpaRepository<EventoPersonagem, Integer> {

    boolean existsByEventoIdAndPersonagemId(Integer eventoId, Integer personagemId);

    List<EventoPersonagem> findByEventoId(Integer eventoId);

    @Query("SELECT COUNT(ep) FROM EventoPersonagem ep " +
           "WHERE ep.personagem.id = :personagemId " +
           "AND ep.evento.dataInicio > CURRENT_TIMESTAMP " +
           "AND ep.evento.status NOT IN " +
           "(br.com.encantada.personageminterno.domain.enums.EventoStatus.CANCELADO, " +
           "br.com.encantada.personageminterno.domain.enums.EventoStatus.FINALIZADO)")
    long countVagasFuturas(@Param("personagemId") Integer personagemId);
}
