package com.nepbrain.technology.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleContextResolver localeContextResolver() {
        AcceptHeaderLocaleContextResolver localeResolver = new AcceptHeaderLocaleContextResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH); //Fallback Locale
        return localeResolver;
    }
}
