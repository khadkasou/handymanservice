package com.nepbrain.technology.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationEventConfig implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("{} started.", serviceName);
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        log.info("{} stopped.", serviceName);
    }
}
