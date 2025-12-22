package com.bifos.batch.file;

import com.bifos.batch.constants.JobNameConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 고정된 길이의 파일 처리 batch를 처리해보는 예제
 * ./gradlew bootRun --args='--spring.batch.job.name=file_fixedSystemFailure_job inputFile=./src/main/resources/fixed-system-failures.txt'
 */
@Slf4j
@Configuration
public class FixedSystemFailureJobConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;


    @Bean(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_JOB)
    public Job systemFailureJob(@Qualifier(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_STEP) Step systemFailureStep) {
        return new JobBuilder(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_JOB, jobRepository)
            .start(systemFailureStep)
            .build();
    }


    @Bean(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_STEP)
    public Step systemFailureStep(
        @Qualifier(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_READER) FlatFileItemReader<SystemFailure> systemFailureFlatFileItemReader,
        SystemFailureStdoutItemWriter systemFailureStdoutItemWriter) {
        return new StepBuilder(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_STEP, jobRepository)
            .<SystemFailure, SystemFailure>chunk(10, transactionManager)
            .reader(systemFailureFlatFileItemReader)
            .writer(systemFailureStdoutItemWriter)
            .build();
    }


    @Bean(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_READER)
    @StepScope
    public FlatFileItemReader<SystemFailure> systemFailureItemReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<SystemFailure>()
            .name(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_READER)
            .resource(new FileSystemResource(inputFile))
            .fixedLength()
            .columns(new Range[]{
                new Range(1, 8),     // errorId: ERR001 + 공백 2칸
                new Range(9, 29),    // errorDateTime: 날짜시간 + 공백 2칸
                new Range(30, 39),   // severity: CRITICAL/FATAL + 패딩
                new Range(40, 45),   // processId: 1234 + 공백 2칸
                new Range(46, 68)    // errorMessage: 메시지 + \n
            })
            .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
            .targetType(SystemFailure.class)
            .build();
    }


    @Bean(JobNameConst.FILE_FIXED_SYSTEM_FAILURE_WRITER)
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
