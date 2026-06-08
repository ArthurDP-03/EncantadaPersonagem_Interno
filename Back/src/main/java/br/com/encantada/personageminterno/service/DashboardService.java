package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.repository.DashboardRepository;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.AlertasResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.EventoMesResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.PersonagemEscaladoResponse;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse.ProximoEventoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                Optional.ofNullable(dashboardRepository.countTotalEventos()).orElse(0L),
                toEventosPorStatus(dashboardRepository.countEventosPorStatus()),
                Optional.ofNullable(dashboardRepository.sumFaturamentoConfirmados()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(dashboardRepository.countAtoresAtivos()).orElse(0L),
                Optional.ofNullable(dashboardRepository.countConvitesPendentes()).orElse(0L),
                Optional.ofNullable(dashboardRepository.countEscalacoesPendentes()).orElse(0L),
                toProximosEventos(dashboardRepository.findProximosEventos()),
                toEventosPorMes(dashboardRepository.findEventosPorMes()),
                toPersonagensMaisEscalados(dashboardRepository.findPersonagensMaisEscalados()),
                toAlertas()
        );
    }

    private AlertasResponse toAlertas() {
        return new AlertasResponse(
                Optional.ofNullable(dashboardRepository.countEventosSemEscalacao()).orElse(0L),
                Optional.ofNullable(dashboardRepository.countEscalacoesPendentes()).orElse(0L),
                Optional.ofNullable(dashboardRepository.countItensEmManutencao()).orElse(0L)
        );
    }

    private Map<String, Long> toEventosPorStatus(List<Object[]> rows) {
        Map<String, Long> map = new HashMap<>();
        rows.forEach(row -> {
            String status = row[0] instanceof Enum<?> e ? e.name() : row[0].toString();
            map.put(status, (Long) row[1]);
        });
        return map;
    }

    private List<ProximoEventoResponse> toProximosEventos(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new ProximoEventoResponse(
                (String) row[0],
                (String) row[1],
                (LocalDateTime) row[2], // era ((Timestamp) row[2]).toLocalDateTime()
                row[3] instanceof Enum<?> e ? e.name() : row[3].toString(),
                (BigDecimal) row[4]
        ))
                .toList();
    }

    private List<EventoMesResponse> toEventosPorMes(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new EventoMesResponse(
                (String) row[0],
                ((Number) row[1]).longValue(),
                (BigDecimal) row[2]
        ))
                .toList();
    }

    private List<PersonagemEscaladoResponse> toPersonagensMaisEscalados(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new PersonagemEscaladoResponse(
                (String) row[0],
                ((Number) row[1]).longValue()
        ))
                .toList();
    }

}
