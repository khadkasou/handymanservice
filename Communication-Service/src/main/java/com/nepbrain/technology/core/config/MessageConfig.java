package com.nepbrain.technology.core.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * MessageSource using classpath:message and classpath:error (aligned with Bucket-Service).
 * Avoids ResourceBundle [messages] not found warning.
 */
@Configuration
public class MessageConfig {

    @Bean
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("classpath:message", "classpath:error");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        return source;
    }
}

