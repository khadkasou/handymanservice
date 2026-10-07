package com.nepbrain.technology.communication.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds auth and user-account email templates from CSV on startup (idempotent per topic/language).
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class MessageInitialDataLoader implements CommandLineRunner {

    private static final String AUTH_USER_MESSAGES_CSV = "data/auth_user_messages_initial.csv";

    @Value("${communication.initial-data.load-on-startup:true}")
    private boolean loadOnStartup;

    private final MessageTemplateCsvLoader messageTemplateCsvLoader;

    @Override
    @Transactional
    public void run(String... args) {
        if (!loadOnStartup) {
            log.debug("Initial data loader disabled (communication.initial-data.load-on-startup=false).");
            return;
        }

        log.info("Loading auth & user account templates from {}...", AUTH_USER_MESSAGES_CSV);
        int loaded = messageTemplateCsvLoader.loadFromClasspath(AUTH_USER_MESSAGES_CSV);
        if (loaded > 0) {
            log.info("Loaded {} auth/user template detail(s).", loaded);
        } else {
            log.info("Auth/user templates already present; nothing new to load.");
        }
    }
}
