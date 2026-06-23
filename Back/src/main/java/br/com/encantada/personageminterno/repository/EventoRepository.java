package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Evento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    boolean existsByClienteId(Integer clienteId);

    @Query("""
            SELECT DISTINCT e
            FROM Evento e
            WHERE EXISTS (
                SELECT es.id
                FROM Escalacao es
                WHERE es.eventoPersonagem.evento = e
                  AND es.ator.email = :atorEmail
                  AND es.status <> br.com.encantada.personageminterno.domain.enums.EscalacaoStatus.CANCELADA
            )
            """)
    List<Evento> findEventosEscaladosDoAtor(@Param("atorEmail") String atorEmail);

    @Query("""
            SELECT COUNT(e) > 0
            FROM Evento e
            WHERE e.id = :eventoId
              AND (
                  EXISTS (
                      SELECT c.id
                      FROM Convite c
                      WHERE c.eventoPersonagem.evento = e
                        AND c.ator.email = :atorEmail
                  )
                  OR EXISTS (
                      SELECT es.id
                      FROM Escalacao es
                      WHERE es.eventoPersonagem.evento = e
                        AND es.ator.email = :atorEmail
                        AND es.status <> br.com.encantada.personageminterno.domain.enums.EscalacaoStatus.CANCELADA
                  )
              )
            """)
    boolean existsEventoVinculadoAoAtor(
            @Param("eventoId") Integer eventoId,
            @Param("atorEmail") String atorEmail);
}
