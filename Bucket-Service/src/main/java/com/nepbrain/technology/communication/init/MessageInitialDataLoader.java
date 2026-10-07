package com.nepbrain.technology.communication.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds auth, user-account, leave, and attendance email templates from a single CSV on startup.
 * Idempotent: only inserts missing topic/language rows; skips unchanged rows.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class MessageInitialDataLoader implements CommandLineRunner {

    private static final String MESSAGES_CSV = "data/messages_initial.csv";

    @Value("${communication.initial-data.load-on-startup:true}")
    private boolean loadOnStartup;

    private final MessageTemplateCsvLoader messageTemplateCsvLoader;

    @Override
    public void run(String... args) {
        if (!loadOnStartup) {
            log.debug("Message template loader disabled (communication.initial-data.load-on-startup=false).");
            return;
        }

        log.info("Loading message templates from {}...", MESSAGES_CSV);
        int loaded = messageTemplateCsvLoader.loadFromClasspath(MESSAGES_CSV);
        if (loaded > 0) {
            log.info("Loaded {} message template detail(s).", loaded);
        } else {
            log.info("Message templates already present; nothing new to load.");
        }
    }
}
