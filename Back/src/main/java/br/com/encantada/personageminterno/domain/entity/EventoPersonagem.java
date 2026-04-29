package br.com.encantada.personageminterno.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "evento_personagem",
        uniqueConstraints = @UniqueConstraint(name = "uq_evento_personagem", columnNames = {"id_evento", "id_personagem"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoPersonagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id_evento_personagem")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personagem", nullable = false)
    private Personagem personagem;
}
