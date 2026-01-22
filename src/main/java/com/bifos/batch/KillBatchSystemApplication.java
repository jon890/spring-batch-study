package com.bifos.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
@EnableJdbcJobRepository
public class KillBatchSystemApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(KillBatchSystemApplication.class, args);
        int exit = SpringApplication.exit(run);
        System.exit(exit); // 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달할 수 있음
    }
}
