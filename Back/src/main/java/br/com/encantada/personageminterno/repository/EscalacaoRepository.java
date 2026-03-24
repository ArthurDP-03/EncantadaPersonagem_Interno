package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Escalacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalacaoRepository extends JpaRepository<Escalacao, Integer> {

    boolean existsByEventoPersonagemId(Integer eventoPersonagemId);
}
