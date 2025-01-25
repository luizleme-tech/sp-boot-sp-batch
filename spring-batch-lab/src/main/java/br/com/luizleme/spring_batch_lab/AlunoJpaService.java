package br.com.luizleme.spring_batch_lab;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoJpaService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void salvar(List<Aluno> alunos) {
        // Dividir em sublistas
        List<List<Aluno>> partitions = Lists.partition(alunos, 1000); // Ajuste conforme necessário
        int totalDeParticoes = partitions.size();
        int particao = 1;

        long inicio = System.currentTimeMillis();
        for (List<Aluno> partition : partitions) {
            System.out.println("Iniciando partição: " + particao + " de: " + totalDeParticoes);
            aquiSalva(partition);
            particao++;
            System.out.println("Finalizando partição: " + particao + " de: " + totalDeParticoes);
        }
        long fim = System.currentTimeMillis();
        System.out.println("EXECUTOU EM " + (fim - inicio) + " ms");
    }

    /**
     * Persiste uma partição de alunos em uma transação separada.
     *
     * @param partition Sublista de alunos a serem salvos.
     */

    protected void aquiSalva(List<Aluno> partition) {
        for (Aluno aluno : partition) {
            entityManager.persist(aluno);
        }
        entityManager.flush();
        entityManager.clear();
    }
}
