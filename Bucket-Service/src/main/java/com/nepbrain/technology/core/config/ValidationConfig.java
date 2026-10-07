package com.nepbrain.technology.core.config;

import com.nepbrain.technology.core.util.ValidationKeyHolder;
import jakarta.validation.MessageInterpolator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Locale;

@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);

        // Wrap the default interpolator AFTER factory initializes
        factory.afterPropertiesSet();
        MessageInterpolator delegate = factory.getMessageInterpolator();
        factory.setMessageInterpolator(new TrackingInterpolator(delegate));

        return factory;
    }

    static class TrackingInterpolator implements MessageInterpolator {
        private final MessageInterpolator delegate;

        TrackingInterpolator(MessageInterpolator delegate) {
            this.delegate = delegate;
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            // messageTemplate is literally "{CMS001}" from your annotation
            ValidationKeyHolder.push(messageTemplate);
            return delegate.interpolate(messageTemplate, context);
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            ValidationKeyHolder.push(messageTemplate);
            return delegate.interpolate(messageTemplate, context, locale);
        }
    }
}

