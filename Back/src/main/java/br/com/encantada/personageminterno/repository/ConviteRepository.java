package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConviteRepository extends JpaRepository<Convite, Integer> {
    List<Convite> findByEventoPersonagemId(Integer eventoPersonagemId);

    List<Convite> findByEventoPersonagemIdAndStatus(Integer epId, ConviteStatus status);

    List<Convite> findByAtorIdAndStatus(Integer atorId, ConviteStatus status);

    List<Convite> findByAtorId(Integer atorId);

    List<Convite> findByAdministradorId(Integer administradorId);

    long countByEventoPersonagemIdAndStatus(Integer epId, ConviteStatus status);

    Optional<Convite> findByEventoPersonagemIdAndAtorId(Integer eventoPersonagemId, Integer atorId);

    @Query("""
            SELECT COUNT(c) > 0
            FROM Convite c
            WHERE c.eventoPersonagem.evento.id = :eventoId
              AND c.ator.id = :atorId
              AND c.eventoPersonagem.id <> :eventoPersonagemId
              AND c.status IN (
                  br.com.encantada.personageminterno.domain.enums.ConviteStatus.PENDENTE,
                  br.com.encantada.personageminterno.domain.enums.ConviteStatus.ACEITO
              )
            """)
    boolean existsAtorComConviteAtivoEmOutroPersonagemDoEvento(
            @Param("eventoId") Integer eventoId,
            @Param("atorId") Integer atorId,
            @Param("eventoPersonagemId") Integer eventoPersonagemId);

    @Query("SELECT c FROM Convite c WHERE c.status = br.com.encantada.personageminterno.domain.enums.ConviteStatus.PENDENTE AND c.dataExpiracao < :agora")
    List<Convite> findPendentesExpirados(LocalDateTime agora);
}
