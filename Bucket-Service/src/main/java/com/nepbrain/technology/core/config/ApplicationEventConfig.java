package com.nepbrain.technology.core.config;

import com.nepbrain.technology.core.payload.request.ApplicationLogRequest;
import com.nepbrain.technology.core.enums.ApplicationStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventConfig implements ApplicationRunner {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${kafka.topic.application}")
    private String applicationLog;

    @Override
    public void run(ApplicationArguments args) {
        // This method will be executed upon application startup
        ApplicationLogRequest applicationLogRequest = ApplicationLogRequest.builder()
                .service(serviceName)
                .status(ApplicationStatusEnum.START).build();
        kafkaTemplate.send(applicationLog, applicationLogRequest);
        log.info("{} started.", serviceName);
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        // This method will be executed upon application stop
        event.getApplicationContext();
        ApplicationLogRequest applicationLogRequest = ApplicationLogRequest.builder()
                .service(serviceName)
                .status(ApplicationStatusEnum.STOP)
                .build();
        kafkaTemplate.send(applicationLog, applicationLogRequest);
        log.info("{} stopped.", serviceName);
    }
}
