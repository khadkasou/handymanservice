package com.nepbrain.technology.communication.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds HRM workflow email templates (leave + attendance) from CSV on startup.
 * Idempotent: only inserts missing topic/language rows.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class HrmMessageInitialDataLoader implements CommandLineRunner {

    private static final String HRM_MESSAGES_CSV = "data/hrm_messages_initial.csv";

    @Value("${communication.initial-data.hrm.load-on-startup:true}")
    private boolean loadOnStartup;

    private final MessageTemplateCsvLoader messageTemplateCsvLoader;

    @Override
    @Transactional
    public void run(String... args) {
        if (!loadOnStartup) {
            log.debug("HRM message loader disabled (communication.initial-data.hrm.load-on-startup=false).");
            return;
        }

        log.info("Loading HRM message templates from {}...", HRM_MESSAGES_CSV);
        int loaded = messageTemplateCsvLoader.loadFromClasspath(HRM_MESSAGES_CSV);
        if (loaded > 0) {
            log.info("Loaded {} HRM message template detail(s).", loaded);
        } else {
            log.info("HRM message templates already present; nothing new to load.");
        }
    }
}
