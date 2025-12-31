package com.bifos.batch.jobparameters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 기본형 타입 잡 파라미터 전달 예제
 * ./gradlew bootRun --args='--spring.batch.job.name=processTerminatorJob terminatorId=KILL-9,java.lang.String targetCount=5,java.lang.Integer'
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemTerminatorConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean
    public Job processTerminatorJob(Step terminationStep) {
        return new JobBuilder("processTerminatorJob", jobRepository)
            .start(terminationStep)
            .build();
    }


    @Bean
    public Step terminationStep(Tasklet terminatorTasklet) {
        return new StepBuilder("terminatorStep", jobRepository)
            .tasklet(terminatorTasklet, transactionManager)
            .build();
    }


    @Bean
    @StepScope
    public Tasklet terminatorTasklet(@Value("#{jobParameters['terminatorId']}") String terminatorId,
                                     @Value("#{jobParameters['targetCount']}") Integer targetCount) {
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
