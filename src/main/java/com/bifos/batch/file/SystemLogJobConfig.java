package com.bifos.batch.file;

import com.bifos.batch.constants.JobNameConst;
import com.bifos.batch.file.entity.SystemLog;
import com.bifos.batch.file.mapper.AbortFieldSetMapper;
import com.bifos.batch.file.mapper.CollectFieldSetMapper;
import com.bifos.batch.file.mapper.ErrorFieldSetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 각 파일 라인의 유형에 맞는 tokenizer, fieldSetMapper를 정의하는 방법 예제
 * Ant 스타일의 패턴 매칭을 지원
 * ./gradlew bootRun --args='--spring.batch.job.name=file_systemLog_job inputFile=./src/main/resources/pattern.log'
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemLogJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean(JobNameConst.FILE_SYSTEM_LOG_JOB)
    public Job systemLogJob(@Qualifier(JobNameConst.FILE_SYSTEM_LOG_STEP) Step systemLogStep) {
        return new JobBuilder(JobNameConst.FILE_SYSTEM_LOG_JOB, jobRepository)
            .start(systemLogStep)
            .build();
    }


    @Bean(JobNameConst.FILE_SYSTEM_LOG_STEP)
    public Step systemLogStep(@Qualifier(JobNameConst.FILE_SYSTEM_LOG_READER) ItemReader<SystemLog> systemLogReader,
                              @Qualifier(JobNameConst.FILE_SYSTEM_LOG_WRITER) ItemWriter<SystemLog> systemLogWriter) {
        return new StepBuilder(JobNameConst.FILE_SYSTEM_LOG_STEP, jobRepository)
            .<SystemLog, SystemLog>chunk(10, transactionManager)
            .reader(systemLogReader)
            .writer(systemLogWriter)
            .build();
    }


    @Bean(JobNameConst.FILE_SYSTEM_LOG_READER)
    @StepScope
    public ItemReader<SystemLog> systemLogReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<SystemLog>()
            .name(JobNameConst.FILE_SYSTEM_LOG_READER)
            .resource(new FileSystemResource(inputFile))
            .lineMapper(systemLogLineMapper())
            .build();
    }


    @Bean
    public PatternMatchingCompositeLineMapper<SystemLog> systemLogLineMapper() {
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("ERROR*", errorLineTokenizer());
        tokenizers.put("ABORT*", abortLineTokenizer());
        tokenizers.put("COLLECT*", collectLineTokenizer());

        Map<String, FieldSetMapper<SystemLog>> mappers = new HashMap<>();
        mappers.put("ERROR*", new ErrorFieldSetMapper());
        mappers.put("ABORT*", new AbortFieldSetMapper());
        mappers.put("COLLECT*", new CollectFieldSetMapper());

        return new PatternMatchingCompositeLineMapper<>(tokenizers, mappers);
    }


    @Bean
    public DelimitedLineTokenizer errorLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "resourceUsage", "logPath");
        return tokenizer;
    }


    @Bean
    public DelimitedLineTokenizer abortLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "exitCode", "processPath", "status");
        return tokenizer;
    }


    @Bean
    public DelimitedLineTokenizer collectLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "dumpType", "processId", "timestamp", "dumpPath");
        return tokenizer;
    }


    @Bean(JobNameConst.FILE_SYSTEM_LOG_WRITER)
    public ItemWriter<SystemLog> systemLogWriter() {
        return items -> {
            for (SystemLog item : items) {
                log.info("{}", item);
            }
        };
    }
}
