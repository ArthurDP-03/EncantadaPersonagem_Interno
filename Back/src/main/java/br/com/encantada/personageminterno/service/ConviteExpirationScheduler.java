package br.com.encantada.personageminterno.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.encantada.personageminterno.domain.entity.Convite;
import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import br.com.encantada.personageminterno.repository.ConviteRepository;

@Component
public class ConviteExpirationScheduler {

    private final ConviteRepository conviteRepository;

    public ConviteExpirationScheduler(ConviteRepository conviteRepository) {
        this.conviteRepository = conviteRepository;
    }

    /** Executa a cada hora e marca como EXPIRADO todo convite PENDENTE com dataExpiracao vencida. */
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void expirarConvitesPendentes() {
        List<Convite> vencidos = conviteRepository.findPendentesExpirados(LocalDateTime.now());
        vencidos.forEach(c -> c.setStatus(ConviteStatus.EXPIRADO));
        conviteRepository.saveAll(vencidos);
    }
}
