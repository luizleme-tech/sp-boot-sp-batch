package br.com.luizleme.spring_batch_lab;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
@Getter
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToOne()
    private Turno turno;

    public Aluno(String nome) {
        this.nome = nome;
    }

    public Aluno(String nome, Turno turno) {
        this(nome);
        this.turno = turno;
    }
}
