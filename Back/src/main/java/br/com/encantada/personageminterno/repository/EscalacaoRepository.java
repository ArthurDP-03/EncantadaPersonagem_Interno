package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Escalacao;
import br.com.encantada.personageminterno.domain.enums.EscalacaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EscalacaoRepository extends JpaRepository<Escalacao, Integer> {

    boolean existsByEventoPersonagemId(Integer eventoPersonagemId);

    boolean existsByEventoPersonagemIdAndStatusNot(
            Integer eventoPersonagemId,
            EscalacaoStatus status);

    List<Escalacao> findByEventoPersonagemEventoId(Integer eventoId);

    @Query("""
            SELECT COUNT(e) > 0
            FROM Escalacao e
            WHERE e.eventoPersonagem.evento.id = :eventoId
              AND e.ator.id = :atorId
              AND e.eventoPersonagem.id <> :eventoPersonagemId
              AND e.status <> br.com.encantada.personageminterno.domain.enums.EscalacaoStatus.CANCELADA
            """)
    boolean existsAtorEscaladoEmOutroPersonagemDoEvento(
            @Param("eventoId") Integer eventoId,
            @Param("atorId") Integer atorId,
            @Param("eventoPersonagemId") Integer eventoPersonagemId);
}
