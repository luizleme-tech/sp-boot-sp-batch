package br.com.luizleme.spring_batch_lab;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private List<Aluno> alunos;

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;

    }
}
