package com.luizlemetech.codetickets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class ImportacaoJobConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoJobConfiguration.class);
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                //.next(moverArquivosStep(jobRepository))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader<Importacao> reader, ItemWriter<Importacao> writer, JobRepository jobRepository) {
        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(10000, transactionManager)
                .allowStartIfComplete(true)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> reader() {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv")
                .resource(new FileSystemResource("files/dados-200k.csv"))
                .comments("--")
                .delimited()
                .delimiter(";")
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor")
                .fieldSetMapper(new ImportacaoMapper())
                .build();
    }

//    @Bean
//    public ItemWriter<Importacao> writer(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<Importacao>()
//                .dataSource(dataSource)
//                .sql(
//                        "INSERT INTO importacao (cpf, cliente, nascimento, evento, data, tipo_ingresso, valor, hora_importacao, taxa_adm) VALUES" +
//                                " (:cpf, :cliente, :nascimento, :evento, :data, :tipoIngresso, :valor, :horaImportacao, :taxaAdm)"
//
//                )
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .build();
//    }

    @Bean
    public ItemWriter<Importacao> writer(ImportacaoRepository importacaoRepository) {
        return items -> {
            importacaoRepository.saveAll(items);
            importacaoRepository.flush();
        };
    }

    @Bean
    public ItemProcessor processor() {
        return new ImportacaoProcessor();
    }

    @Bean
    public Tasklet moverArquivosTasklet() {
        return (contribution, chunkContext) -> {
            File pastaOrigem = new File("files");
            File pastaDestino = new File("imported-files");

            if (!pastaDestino.exists()) {
                pastaDestino.mkdirs();
            }

            File[] arquivos = pastaOrigem.listFiles((dir, name) -> name.endsWith(".csv"));

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    File arquivoDestino = new File(pastaDestino, arquivo.getName());
                    if (arquivo.renameTo(arquivoDestino)) {
                        System.out.println("Arquivo movido: " + arquivo.getName());
                    } else {
                        throw new RuntimeException("Não foi possível mover o arquivo: " + arquivo.getName());
                    }
                }
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step moverArquivosStep(JobRepository jobRepository) {
        return new StepBuilder("mover-arquivo", jobRepository)
                .tasklet(moverArquivosTasklet(), transactionManager)
                .build();
    }
}

