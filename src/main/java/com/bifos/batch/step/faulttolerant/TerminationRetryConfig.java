package com.bifos.batch.step.faulttolerant;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

/**
 * 내결함성 동작 검증 코드
 * ./gradlew bootRun --args='--spring.batch.job.name=terminationRetryJob'
 */
@Configuration
@RequiredArgsConstructor
public class TerminationRetryConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean
    public Job terminationRetryJob() {
        return new JobBuilder("terminationRetryJob", jobRepository)
            .start(terminationRetryStep())
            .build();
    }


    @Bean
    public Step terminationRetryStep() {
        return new StepBuilder("terminationRetryStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(terminationRetryItemReader())
            .processor(terminationRetryProcessor())
            .writer(terminationRetryItemWriter())
            .faultTolerant()
            .retry(TerminationFailedException.class)
            .retryLimit(3)
            .listener(terminationRetryListener())
            .build();
    }


    @Bean
    public ListItemReader<Scream> terminationRetryItemReader() {
        return new ListItemReader<>(List.of(
            Scream.builder()
                  .id(1)
                  .scream("멈춰")
                  .processMsg("멈추라고 했는데 안 들음.")
                  .build(),
            Scream.builder()
                  .id(2)
                  .scream("제발")
                  .processMsg("애원 소리 귀찮네.")
                  .build(),
            Scream.builder()
                  .id(3)
                  .scream("살려줘")
                  .processMsg("구조 요청 무시.")
                  .build(),
            Scream.builder()
                  .id(4)
                  .scream("으악")
                  .processMsg("디스크 터지며 울부짖음.")
                  .build(),
            Scream.builder()
                  .id(5)
                  .scream("끄아악")
                  .processMsg("메모리 붕괴 비명.")
                  .build(),
            Scream.builder()
                  .id(6)
                  .scream("System.exit(-666)")
                  .processMsg("초살 프로토콜 발동.")
                  .build())) {
            @Override
            public Scream read() {
                Scream scream = super.read();
                if (scream == null) {
                    return null;
                }
                System.out.println("[ItemReader]: 처형 대상 = " + scream);
                return scream;
            }
        };
    }


    @Bean
    public ItemProcessor<Scream, Scream> terminationRetryProcessor() {
        return new ItemProcessor<>() {
            private static final int MAX_PATIENCE = 3;
            private int mercy = 0; // 자비 카운트


            @Override
            public @NonNull Scream process(Scream item) {
                System.out.println("[ItemProcessor]: 처형 대상 = " + item);

                if (item.getId() == 3 && mercy < MAX_PATIENCE) {
                    mercy++;
                    System.out.println("-> 처형 실패");
                    throw new TerminationFailedException("처형 거부자 = " + item);
                } else {
                    System.out.println("-> 처형 완료 (" + item.getProcessMsg() + ")");
                }

                return item;
            }
        };
    }


    @Bean
    public ItemWriter<Scream> terminationRetryItemWriter() {
        return chunk -> {
            System.out.println("[ItemWriter]: 처형 기록 시작. 기록 대상 = " + chunk.getItems());

            for (Scream scream : chunk) {
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }


    @Bean
    public RetryListener terminationRetryListener() {
        return new RetryListener() {
            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                System.out.println("SYSTEM: 이것 봐라? 안 죽네? " + throwable + " (현재 총 시도 횟수=" + context.getRetryCount() + "). 다시 처형한다.\n");
            }
        };
    }

}
