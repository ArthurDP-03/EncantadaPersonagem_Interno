package br.com.encantada.personageminterno.domain.entity;

import br.com.encantada.personageminterno.domain.enums.ConviteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convite", uniqueConstraints = @UniqueConstraint(name = "uq_convite", columnNames = {"id_evento_personagem", "id_ator"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convite")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento_personagem", nullable = false)
    private EventoPersonagem eventoPersonagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ator", nullable = false)
    private Ator ator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_administrador", nullable = false)
    private Administrador administrador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConviteStatus status;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    private LocalDateTime dataResposta;
}
