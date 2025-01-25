package br.com.luizleme.spring_batch_lab;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;

    public Turno() {}

    public Turno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
