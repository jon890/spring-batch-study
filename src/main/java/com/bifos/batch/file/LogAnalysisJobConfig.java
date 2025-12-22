package com.bifos.batch.file;

import com.bifos.batch.constants.JobNameConst;
import com.bifos.batch.file.entity.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 정규식 기반의 토크나이저 사용하기
 * ./gradlew bootRun --args='--spring.batch.job.name=file_logAnalysis_job inputFile=./src/main/resources/regex-log.txt'
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogAnalysisJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean(JobNameConst.FILE_LOG_ANALYSIS_JOB)
    public Job logAnalysisJob(@Qualifier(JobNameConst.FILE_LOG_ANALYSIS_STEP) Step logAnalysisStep) {
        return new JobBuilder(JobNameConst.FILE_LOG_ANALYSIS_JOB, jobRepository)
            .start(logAnalysisStep)
            .build();
    }


    @Bean(JobNameConst.FILE_LOG_ANALYSIS_STEP)
    public Step logAnalysisStep(@Qualifier(JobNameConst.FILE_LOG_ANALYSIS_READER) ItemReader<LogEntry> logItemReader,
                                @Qualifier(JobNameConst.FILE_LOG_ANALYSIS_WRITER) ItemWriter<LogEntry> logItemWriter) {
        return new StepBuilder(JobNameConst.FILE_LOG_ANALYSIS_STEP, jobRepository)
            .<LogEntry, LogEntry>chunk(10, transactionManager)
            .reader(logItemReader)
            .writer(logItemWriter)
            .build();
    }


    @Bean(JobNameConst.FILE_LOG_ANALYSIS_READER)
    @StepScope
    public FlatFileItemReader<LogEntry> logItemReader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        RegexLineTokenizer tokenizer = new RegexLineTokenizer();
        tokenizer.setRegex("\\[\\w+\\]\\[Thread-(\\d+)\\]\\[CPU: \\d+%\\] (.+)");

        return new FlatFileItemReaderBuilder<LogEntry>()
            .name(JobNameConst.FILE_LOG_ANALYSIS_READER)
            .resource(new FileSystemResource(inputFile))
            .lineTokenizer(tokenizer)
            .fieldSetMapper(fieldSet -> new LogEntry(fieldSet.readString(0), fieldSet.readString(1)))
            .build();
    }


    @Bean(JobNameConst.FILE_LOG_ANALYSIS_WRITER)
    public ItemWriter<LogEntry> logItemWriter() {
        return items -> {
            for (LogEntry logEntry : items) {
                log.info(String.format("THD-%s: %s",
                                       logEntry.getThreadNum(), logEntry.getMessage()));
            }
        };
    }
}
