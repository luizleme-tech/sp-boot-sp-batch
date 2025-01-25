package br.com.luizleme.spring_batch_lab;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlunoItemReader implements ItemReader<Aluno> {
    private final AlunoService alunoService;
    private List<Aluno> alunos;
    private int nextAlunoIndex;

    @Autowired
    public AlunoItemReader(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Override
    public Aluno read() {
        if (alunos == null) {
            alunos = alunoService.getAlunos();
        }

        Aluno nextAluno = null;

        if (nextAlunoIndex < alunos.size()) {
            nextAluno = alunos.get(nextAlunoIndex);
            nextAlunoIndex++;
        }

        return nextAluno;
    }
}
