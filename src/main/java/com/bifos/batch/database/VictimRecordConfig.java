package com.bifos.batch.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ./gradlew bootRun --args='--spring.batch.job.name=victimRecordJob'
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class VictimRecordConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;


    @Bean
    public Job processVictimJob(Step processVictimStep) {
        return new JobBuilder("victimRecordJob", jobRepository)
            .start(processVictimStep)
            .build();
    }


    @Bean
    public Step processVictimStep(ItemReader<Victim> terminatedVictimReader,
                                  ItemWriter<Victim> victimWriter) {
        return new StepBuilder("victimRecordStep", jobRepository)
            .<Victim, Victim>chunk(5, transactionManager)
            .reader(terminatedVictimReader)
            .writer(victimWriter)
            .build();
    }

    // JdbcCursorItemReader 예제
    //    @Bean
    //    public JdbcCursorItemReader<Victim> terminatedVictimReader() {
    //        return new JdbcCursorItemReaderBuilder<Victim>()
    //            .name("terminatedVictimReader")
    //            .dataSource(dataSource)
    //            .sql("SELECT * FROM victims WHERE status = ? AND terminated_at <= ?")
    //            .queryArguments(List.of("TERMINATED", LocalDateTime.now()))
    //            .beanRowMapper(Victim.class)
    //            .build();
    //    }


    // JdbcPagingItemReader 예제
    @Bean
    public JdbcPagingItemReader<Victim> terminatedVictimReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<Victim>()
            .name("terminatedVictimReader")
            .dataSource(dataSource)
            .pageSize(5)
            .selectClause("SELECT id, name, process_id, terminated_at, status")
            .fromClause("FROM victims")
            .whereClause("WHERE status = :status AND terminated_at <= :terminatedAt")
            .sortKeys(Map.of("id", Order.ASCENDING))
            .parameterValues(Map.of(
                "status", "TERMINATED",
                "terminatedAt", LocalDateTime.now()
                                   ))
            .beanRowMapper(Victim.class)
            .build();
    }


    @Bean
    public ItemWriter<Victim> victimWriter() {
        return items -> {
            for (Victim victim : items) {
                log.info("{}", victim);
            }
        };
    }


    @NoArgsConstructor
    @Data
    public static class Victim {
        private Long id;
        private String name;
        private String processId;
        private LocalDateTime terminatedAt;
        private String status;
    }
}
