package io.github.jvanheesch.aci;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.commandhandling.gateway.RetryScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RetryScheduler retryScheduler() {
        return IntervalRetryScheduler.builder()
                .maxRetryCount(1)
                .retryExecutor(new ScheduledThreadPoolExecutor(1))
                .build();
    }

    @Bean
    public CommandGateway commandGateway(CommandBus commandBus, RetryScheduler retryScheduler) {
        return DefaultCommandGateway.builder()
                .commandBus(commandBus)
                .retryScheduler(retryScheduler)
                .build();
    }
}
