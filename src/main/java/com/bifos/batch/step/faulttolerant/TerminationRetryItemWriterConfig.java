package com.bifos.batch.step.faulttolerant;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
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
 * ItemWriter에서 내결함성 동작 검증 코드
 * ./gradlew bootRun --args='--spring.batch.job.name=terminationRetryItemWriterJob'
 */
@Configuration
@RequiredArgsConstructor
public class TerminationRetryItemWriterConfig {

    private static final String JOB_NAME = "terminationRetryItemWriterJob";
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean(JOB_NAME)
    public Job terminationRetryJob() {
        return new JobBuilder("terminationRetryItemWriterJob", jobRepository)
            .start(terminationRetryStep())
            .build();
    }


    @Bean(JOB_NAME + "_step")
    public Step terminationRetryStep() {
        return new StepBuilder(JOB_NAME, jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(terminationRetryItemReader())
            .processor(terminationRetryProcessor())
            .writer(terminationRetryWriter())
            .faultTolerant()
            .retry(TerminationFailedException.class)
            .retryLimit(3)
            .listener(terminationRetryListener())
            .build();
    }


    @Bean(JOB_NAME + "_reader")
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


    @Bean(JOB_NAME + "_processor")
    public ItemProcessor<Scream, Scream> terminationRetryProcessor() {
        return scream -> {
            System.out.print("[ItemProcessor]: 처형 대상 = " + scream + "\n");
            return scream;
        };
    }


    @Bean(JOB_NAME + "_writer")
    public ItemWriter<Scream> terminationRetryWriter() {
        return new ItemWriter<>() {
            private static final int MAX_PATIENCE = 2;
            private int mercy = 0;  // 자비 카운트


            @Override
            public void write(@NonNull Chunk<? extends Scream> items) {
                System.out.println("[ItemWriter]: 기록 시작. 처형된 아이템들 = " + items);

                for (Scream scream : items) {
                    if (scream.getId() == 3 && mercy < MAX_PATIENCE) {
                        mercy++;
                        System.out.println("[ItemWriter]: ❌ 기록 실패. 저항하는 아이템 발견 = " + scream);
                        throw new TerminationFailedException("기록 거부자 = " + scream);
                    }
                    System.out.println("[ItemWriter]: ✅ 기록 완료. 처형된 아이템 = " + scream);
                }
            }
        };

    }


    @Bean(JOB_NAME + "_retryListener")
    public RetryListener terminationRetryListener() {
        return new RetryListener() {
            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                System.out.println("SYSTEM: 이것 봐라? 안 죽네? " + throwable + " (현재 총 시도 횟수=" + context.getRetryCount() + "). 다시 처형한다.\n");
            }
        };
    }
}
