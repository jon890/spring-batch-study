package com.bifos.batch.listener;

import com.bifos.batch.constants.JobNameConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * ExecutionContextPromotionListener를 활용한 Step간 데이터 공유 예제
 * ./gradlew bootRun --args='--spring.batch.job.name=listener_systemTermination_job'
 */
@Slf4j
@Configuration
public class SystemTerminationConfig {

    @Bean(JobNameConst.LISTENER_SYSTEM_TERMINATION_JOB)
    public Job systemTerminationJob(JobRepository jobRepository,
                                    @Qualifier(JobNameConst.LISTENER_SYSTEM_TERMINATION_SCANNING_STEP) Step scanningStep,
                                    @Qualifier(JobNameConst.LISTENER_SYSTEM_TERMINATION_ELIMINATION_STEP) Step eliminationStep) {
        return new JobBuilder(JobNameConst.LISTENER_SYSTEM_TERMINATION_JOB, jobRepository)
            .start(scanningStep)
            .next(eliminationStep)
            .build();
    }


    @Bean(JobNameConst.LISTENER_SYSTEM_TERMINATION_SCANNING_STEP)
    public Step scanningStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(JobNameConst.LISTENER_SYSTEM_TERMINATION_SCANNING_STEP, jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String target = "판교 서버실";
                ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
                stepContext.put("targetSystem", target);
                log.info("타겟 스캔 완료: {}", target);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .listener(promotionListener())
            .build();
    }


    @Bean(JobNameConst.LISTENER_SYSTEM_TERMINATION_ELIMINATION_STEP)
    public Step eliminationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                @Qualifier(JobNameConst.LISTENER_SYSTEM_TERMINATION_ELIMINATION_TASKLET) Tasklet eliminationTasklet) {
        return new StepBuilder(JobNameConst.LISTENER_SYSTEM_TERMINATION_ELIMINATION_STEP, jobRepository)
            .tasklet(eliminationTasklet, transactionManager)
            .build();
    }


    @Bean(JobNameConst.LISTENER_SYSTEM_TERMINATION_ELIMINATION_TASKLET)
    @StepScope
    public Tasklet eliminationTasklet(@Value("#{jobExecutionContext['targetSystem']}") String target) {
        return (contribution, chunkContext) -> {
            log.info("시스템 제거 작업 실행: {}", target);
            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"targetSystem"});
        return listener;
    }
}
