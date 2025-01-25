package br.com.luizleme.spring_batch_lab;

import jakarta.transaction.Transactional;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private AlunoItemReader alunoItemReader;


    @Bean
    public Job processJob(Step processStep, JobRepository jobRepository) {
        return new JobBuilder("processJob",jobRepository)
                .start(processStep)
                .build();
    }

    @Bean
    public Step processStep(ItemWriter<Aluno> writer,JobRepository jobRepository) {
        return new StepBuilder("processStep",jobRepository)
                .<Aluno,Aluno>chunk(2500, transactionManager)
                .reader(alunoItemReader)
                .writer(writer)
                .build();
    }

    @Bean
    @Transactional
    public ItemWriter<Aluno> writer(AlunoRepository alunoRepository) {
        return items -> {
            alunoRepository.saveAll(items);
            alunoRepository.flush();
        };
    }
}
