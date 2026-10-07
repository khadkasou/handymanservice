package com.nepbrain.technology.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public ReloadableResourceBundleMessageSource fileMessageSource() {
        ReloadableResourceBundleMessageSource file = new ReloadableResourceBundleMessageSource();
        file.setBasenames("classpath:message", "classpath:error"); // e.g. message.properties, error.properties
        file.setDefaultEncoding("UTF-8");
        file.setFallbackToSystemLocale(false);
        return file;
    }
}

