package com.bifos.batch.file;

import com.bifos.batch.constants.JobNameConst;
import com.bifos.batch.file.entity.SystemFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 파일 처리 batch를 처리해보는 예제
 * ./gradlew bootRun --args='--spring.batch.job.name=file_systemFailure_job inputFile=./src/main/resources/system-failures.csv'
 */
@Slf4j
@Configuration
public class SystemFailureJobConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;


    @Bean(JobNameConst.FILE_SYSTEM_FAILURE_JOB)
    public Job systemFailureJob(@Qualifier(JobNameConst.FILE_SYSTEM_FAILURE_STEP) Step systemFailureStep) {
        return new JobBuilder(JobNameConst.FILE_SYSTEM_FAILURE_JOB, jobRepository)
            .start(systemFailureStep)
            .build();
    }


    @Bean(JobNameConst.FILE_SYSTEM_FAILURE_STEP)
    public Step systemFailureStep(
        @Qualifier(JobNameConst.FILE_SYSTEM_FAILURE_READER) FlatFileItemReader<SystemFailure> systemFailureFlatFileItemReader,
        SystemFailureStdoutItemWriter systemFailureStdoutItemWriter) {
        return new StepBuilder(JobNameConst.FILE_SYSTEM_FAILURE_STEP, jobRepository)
            .<SystemFailure, SystemFailure>chunk(10, transactionManager)
            .reader(systemFailureFlatFileItemReader)
            .writer(systemFailureStdoutItemWriter)
            .build();
    }


    @Bean(JobNameConst.FILE_SYSTEM_FAILURE_READER)
    @StepScope
    public FlatFileItemReader<SystemFailure> systemFailureItemReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<SystemFailure>()
            .name(JobNameConst.FILE_SYSTEM_FAILURE_READER)
            .resource(new FileSystemResource(inputFile))
            .delimited() // LineMapper를 DelimitedLineTokenizer로 지정
            .delimiter(",")
            .names("errorId",
                   "errorDateTime",
                   "severity",
                   "processId",
                   "errorMessage") // 파일에서 읽어들인 데이터의 각 토큰과 순서대로 1:1로 매핑
            .targetType(SystemFailure.class)
            .linesToSkip(1) // 헤더 처리
            .build();
    }


    @Bean(JobNameConst.FILE_SYSTEM_FAILURE_WRITER)
    public SystemFailureStdoutItemWriter systemFailureStdoutItemWriter() {
        return new SystemFailureStdoutItemWriter();
    }


    public static class SystemFailureStdoutItemWriter implements ItemWriter<SystemFailure> {
        @Override
        public void write(Chunk<? extends SystemFailure> chunk) throws Exception {
            for (SystemFailure failure : chunk) {
                log.info("Processing system failure: {}", failure);
            }
        }
    }
}
