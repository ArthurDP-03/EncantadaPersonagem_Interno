package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
}
