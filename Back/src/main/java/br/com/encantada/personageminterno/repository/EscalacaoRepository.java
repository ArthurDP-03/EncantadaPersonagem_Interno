package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Escalacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscalacaoRepository extends JpaRepository<Escalacao, Integer> {

    boolean existsByEventoPersonagemId(Integer eventoPersonagemId);

    List<Escalacao> findByEventoPersonagemEventoId(Integer eventoId);
}
