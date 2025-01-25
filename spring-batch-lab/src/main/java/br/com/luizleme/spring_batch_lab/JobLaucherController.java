package br.com.luizleme.spring_batch_lab;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class JobLaucherController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job processJob;
    @Autowired
    private AlunoService alunoService;
    @Autowired
    private AlunoJpaService jpaService;
    @Autowired
    private TurnoRepository turnoRepository;

    @GetMapping("/run-batch")
    public String handle(){
        List<Aluno> alunoList = generatedList2();


        try {
            alunoService.setAlunos(alunoList);

            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(processJob, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("/run-jpa")
    public String execucaoThreads(){
        List<Aluno> alunoList = generatedList2();
        jpaService.salvar(alunoList);
        return "success";
    }


    private String serializa(List<Aluno> alunoList) {
        return alunoList.stream().map(aluno -> aluno.toString()).collect(Collectors.joining(","));
    }

    private List<Aluno> generatedList() {
        return List.of(new Aluno("Jose"), new Aluno("Carmela"),
                new Aluno("Martin"), new Aluno("Carlos"),
                new Aluno("Luiz"));
    }

    private List<Aluno> generatedList2() {
        List<Aluno> alunoList = new ArrayList<>();
        Turno manha = pesquisarTurno(1L);
        Turno tarde = pesquisarTurno(2L);
        Turno noite = pesquisarTurno(3L);

        for(int i=0; i < 50000; i++) {
            if(i<20000) {
                //turno manha
                alunoList.add(new Aluno("Aluno " + i + "da " + manha.getDescricao(), manha));
            } else if (i>20000 && i<40000) {
                alunoList.add(new Aluno("Aluno " + i + "da " + tarde.getDescricao(), tarde));
            } else {
                alunoList.add(new Aluno("Aluno " + i + "da " + noite.getDescricao(), noite));
            }
        }
        return alunoList;
    }


    private Turno pesquisarTurno(final long id) {
        return turnoRepository.findById(id).orElse(null);
    }
}
