package br.com.encantada.personageminterno.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "escalacao", uniqueConstraints = @UniqueConstraint(name = "uq_escalacao_ep", columnNames = "id_evento_personagem"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escalacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escalacao")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento_personagem", nullable = false)
    private EventoPersonagem eventoPersonagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ator", nullable = false)
    private Ator ator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personagem_item", nullable = false)
    private PersonagemItem personagemItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_administrador", nullable = false)
    private Administrador administrador;

    @Column(nullable = false)
    private LocalDateTime dataEscolha;
}
