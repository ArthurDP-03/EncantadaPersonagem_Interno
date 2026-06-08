package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.EventoPersonagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoPersonagemRepository extends JpaRepository<EventoPersonagem, Integer> {

    boolean existsByEventoIdAndPersonagemId(Integer eventoId, Integer personagemId);

    List<EventoPersonagem> findByEventoId(Integer eventoId);
}
