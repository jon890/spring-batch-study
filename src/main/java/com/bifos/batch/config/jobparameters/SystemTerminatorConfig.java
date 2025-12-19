package com.bifos.batch.config.jobparameters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SystemTerminatorConfig {

    @Bean(name = "ProcessTerminatorJob")
    public Job processTerminatorJob(JobRepository jobRepository, Step terminationStep) {
        return new JobBuilder("ProcessTerminatorJob", jobRepository)
                .start(terminationStep)
                .build();
    }

    @Bean(name = "ProcessTerminatorJob_TerminationStep")
    public Step terminationStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                @Qualifier("ProcessTerminatorJob_TerminationTasklet") Tasklet terminationTasklet) {
        return new StepBuilder("ProcessTerminatorJob_TerminationStep", jobRepository)
                .tasklet(terminationTasklet, transactionManager)
                .build();
    }

    @Bean(name = "ProcessTerminatorJob_TerminationTasklet")
    @StepScope
    public Tasklet terminationTasklet(
            @Value("#{jobParameters['terminatorId']}") String terminatorId,
            @Value("#{jobParameters['targetCount']}") Integer targetCount
    ) {
        return (contribution, chunkContext) -> {
            log.info("시스템 종결자 정보:");
            log.info("ID: {}", terminatorId);
            log.info("제거 대상 수: {}", targetCount);
            log.info("⚡ SYSTEM TERMINATOR {} 작전을 개시합니다.", terminatorId);
            log.info("☠️ {}개의 프로세스를 종료합니다.", targetCount);

            for (int i = 1; i <= targetCount; i++) {
                log.info("💀 프로세스 {} 종료 완료!", i);
            }

            log.info("🎯 임무 완료: 모든 대상 프로세스가 종료되었습니다.");
            return RepeatStatus.FINISHED;
        };
    }
}
