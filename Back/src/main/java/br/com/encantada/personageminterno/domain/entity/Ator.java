package br.com.encantada.personageminterno.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ator")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String genero;

    @Column(precision = 5, scale = 2)
    private BigDecimal altura;

    @Column(precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getGenero() {
        return genero;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public String getObservacao() {
        return observacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
