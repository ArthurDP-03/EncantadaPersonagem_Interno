package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.repository.DashboardRepository;
import br.com.encantada.personageminterno.web.dto.dashboard.DashboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveMontarDashboardComDados() {

        when(dashboardRepository.countTotalEventos())
                .thenReturn(10L);

        when(dashboardRepository.countEventosPorStatus())
                .thenReturn(List.<Object[]>of(
                        new Object[]{"CONFIRMADO", 5L},
                        new Object[]{"PENDENTE", 3L}
                ));

        when(dashboardRepository.sumFaturamentoConfirmados())
                .thenReturn(new BigDecimal("5000.00"));

        when(dashboardRepository.countAtoresAtivos())
                .thenReturn(20L);

        when(dashboardRepository.countConvitesPendentes())
                .thenReturn(4L);

        when(dashboardRepository.countEscalacoesPendentes())
                .thenReturn(2L);

        when(dashboardRepository.findProximosEventos())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                "Festa Infantil",
                                "Cliente Teste",
                                LocalDateTime.of(2026, 1, 10, 14, 0),
                                "CONFIRMADO",
                                new BigDecimal("1200.00")
                        }
                ));

        when(dashboardRepository.findEventosPorMes())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                "JAN",
                                8L,
                                new BigDecimal("3000.00")
                        }
                ));

        when(dashboardRepository.findPersonagensMaisEscalados())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                "Homem-Aranha",
                                15L
                        }
                ));

        when(dashboardRepository.countEventosSemEscalacao())
                .thenReturn(1L);

        when(dashboardRepository.countItensEmManutencao())
                .thenReturn(2L);

        DashboardResponse response =
                dashboardService.getDashboard();

        assertNotNull(response);

        assertEquals(10L, response.totalEventos());

        assertEquals(
                5L,
                response.eventosPorStatus().get("CONFIRMADO")
        );

        assertEquals(
                new BigDecimal("5000.00"),
                response.faturamentoTotal()
        );

        assertEquals(
                20L,
                response.totalAtoresAtivos()
        );

        assertEquals(
                1,
                response.proximosEventos().size()
        );

        assertEquals(
                "Festa Infantil",
                response.proximosEventos().get(0).titulo()
        );

        assertEquals(
                "Homem-Aranha",
                response.personagensMaisEscalados().get(0).personagem()
        );

        assertEquals(
                1L,
                response.alertas().eventosSemEscalacao()
        );

        assertEquals(
                2L,
                response.alertas().itensManutencao()
        );
    }

    @Test
    void deveRetornarValoresPadraoQuandoRepositorioRetornarNull() {

        when(dashboardRepository.countTotalEventos())
                .thenReturn(null);

        when(dashboardRepository.countEventosPorStatus())
                .thenReturn(List.of());

        when(dashboardRepository.sumFaturamentoConfirmados())
                .thenReturn(null);

        when(dashboardRepository.countAtoresAtivos())
                .thenReturn(null);

        when(dashboardRepository.countConvitesPendentes())
                .thenReturn(null);

        when(dashboardRepository.countEscalacoesPendentes())
                .thenReturn(null);

        when(dashboardRepository.findProximosEventos())
                .thenReturn(List.of());

        when(dashboardRepository.findEventosPorMes())
                .thenReturn(List.of());

        when(dashboardRepository.findPersonagensMaisEscalados())
                .thenReturn(List.of());

        when(dashboardRepository.countEventosSemEscalacao())
                .thenReturn(null);

        when(dashboardRepository.countItensEmManutencao())
                .thenReturn(null);

        DashboardResponse response =
                dashboardService.getDashboard();

        assertEquals(0L, response.totalEventos());
        assertEquals(BigDecimal.ZERO, response.faturamentoTotal());
        assertEquals(0L, response.totalAtoresAtivos());
        assertEquals(0L, response.convitesPendentes());
        assertEquals(0L, response.escalacoesPendentes());

        assertTrue(response.eventosPorStatus().isEmpty());
        assertTrue(response.proximosEventos().isEmpty());
        assertTrue(response.eventosPorMes().isEmpty());
        assertTrue(response.personagensMaisEscalados().isEmpty());

        assertEquals(
                0L,
                response.alertas().eventosSemEscalacao()
        );

        assertEquals(
                0L,
                response.alertas().escalacoesPendentes7Dias()
        );

        assertEquals(
                0L,
                response.alertas().itensManutencao()
        );
    }
}