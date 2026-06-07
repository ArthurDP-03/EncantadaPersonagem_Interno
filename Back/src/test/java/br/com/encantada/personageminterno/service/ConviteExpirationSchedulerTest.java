package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.repository.ConviteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConviteExpirationSchedulerTest {

    @Mock
    private ConviteRepository conviteRepository;

    @InjectMocks
    private ConviteExpirationScheduler scheduler;

    @Test
    void deveExpirarConvitesPendentes() {

        Convite convite1 = Convite.builder()
                .id(1)
                .status(ConviteStatus.PENDENTE)
                .build();

        Convite convite2 = Convite.builder()
                .id(2)
                .status(ConviteStatus.PENDENTE)
                .build();

        when(conviteRepository.findPendentesExpirados(any(LocalDateTime.class)))
                .thenReturn(List.of(convite1, convite2));

        scheduler.expirarConvitesPendentes();

        verify(conviteRepository).saveAll(anyList());

        org.junit.jupiter.api.Assertions.assertEquals(
                ConviteStatus.EXPIRADO,
                convite1.getStatus()
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                ConviteStatus.EXPIRADO,
                convite2.getStatus()
        );
    }

    @Test
    void deveSalvarListaVaziaQuandoNaoExistiremConvitesExpirados() {

        when(conviteRepository.findPendentesExpirados(any(LocalDateTime.class)))
                .thenReturn(List.of());

        scheduler.expirarConvitesPendentes();

        verify(conviteRepository).saveAll(List.of());
    }
}