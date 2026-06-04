package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.EventoMesResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.PersonagemEscaladoResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.ProximoEventoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import br.com.encantada.personageminterno.domain.entity.Evento;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Evento, Integer> {

    @Query("SELECT COUNT(e) FROM Evento e")
    Long countTotalEventos();

    @Query("SELECT e.status, COUNT(e) FROM Evento e GROUP BY e.status")
    List<Object[]> countEventosPorStatus();

    @Query("SELECT COALESCE(SUM(e.valorTotal), 0) FROM Evento e WHERE e.status = 'CONFIRMADO'")
    BigDecimal sumFaturamentoConfirmados();

    @Query("SELECT COUNT(a) FROM Ator a WHERE a.ativo = true")
    Long countAtoresAtivos();

    @Query("SELECT COUNT(c) FROM Convite c WHERE c.status = 'PENDENTE'")
    Long countConvitesPendentes();

    @Query("SELECT COUNT(es) FROM Escalacao es WHERE es.status = 'PENDENTE_CONFIRMACAO_ATOR'")
    Long countEscalacoesPendentes();

    @Query("""
            SELECT e.titulo, c.nome, e.dataInicio, e.status, e.valorTotal
            FROM Evento e
            JOIN e.cliente c
            WHERE e.dataInicio >= CURRENT_TIMESTAMP
            ORDER BY e.dataInicio ASC
            LIMIT 5
            """)
    List<Object[]> findProximosEventos();

    @Query(value = """
            SELECT DATE_FORMAT(data_inicio, '%Y-%m') AS mes,
                   COUNT(*) AS quantidade,
                   COALESCE(SUM(valor_total), 0) AS faturamento
            FROM evento
            GROUP BY mes
            ORDER BY mes DESC
            LIMIT 6
            """, nativeQuery = true)
    List<Object[]> findEventosPorMes();

    @Query(value = """
            SELECT p.nome, COUNT(es.id_escalacao) AS total
            FROM escalacao es
            JOIN evento_personagem ep ON es.id_evento_personagem = ep.id_evento_personagem
            JOIN personagem p ON ep.id_personagem = p.id
            GROUP BY p.nome
            ORDER BY total DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findPersonagensMaisEscalados();

    @Query(value = """
            SELECT COUNT(DISTINCT ep.id_evento_personagem)
            FROM evento_personagem ep
            LEFT JOIN escalacao es ON ep.id_evento_personagem = es.id_evento_personagem
            WHERE es.id_escalacao IS NULL
            """, nativeQuery = true)
    Long countEventosSemEscalacao();

    @Query("SELECT COUNT(pi) FROM PersonagemItem pi WHERE pi.status = 'MANUTENCAO'")
    Long countItensEmManutencao();
}