package br.com.encantada.personageminterno.repository;

import br.com.encantada.personageminterno.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
