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
@Table(name = "Escalacao", uniqueConstraints = @UniqueConstraint(name = "UQ_Escalacao_EP", columnNames = "idEventoPersonagem"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escalacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEscalacao")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEventoPersonagem", nullable = false)
    private EventoPersonagem eventoPersonagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idAtor", nullable = false)
    private Ator ator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPersonagemItem", nullable = false)
    private PersonagemItem personagemItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idAdministrador", nullable = false)
    private Administrador administrador;

    @Column(nullable = false)
    private LocalDateTime dataEscolha;
}
