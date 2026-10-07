package com.nepbrain.technology.apigateway.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageBundle {

    private static final Logger logger = LoggerFactory.getLogger(MessageBundle.class);
    private static final String ERROR_PROPERTIES_BASE = "errors";
    private static final String MESSAGE_PROPERTIES_BASE = "messages";

    private MessageBundle() {
    }


    public static String getErrorMessage(String key) {
        return getMessage(key, ERROR_PROPERTIES_BASE);
    }


    public static String getMessage(String key) {
        return getMessage(key, MESSAGE_PROPERTIES_BASE);
    }


    private static String getMessage(String key, String file) {
        try {
            String lang = LocaleContextHolder.getLocale().getLanguage();
            Properties prop = loadPropertiesWithFallback(file, lang);
            return prop.getProperty(key);
        } catch (Exception e) {
            logger.error("Error occurred while reading file {}", file, e);
        }
        return null;
    }

    private static Properties loadPropertiesWithFallback(String file, String lang)
            throws IOException {
        Properties prop = new Properties();
        String localeSpecificFileName = file + "_" + lang + ".properties";
        InputStream input = getResourceAsStream(localeSpecificFileName);
        if (input != null) {
            prop.load(input);
        } else {
            String defaultFileName = file + ".properties";
            input = getResourceAsStream(defaultFileName);
            if (input != null) {
                prop.load(input);
            } else {
                logger.warn(
                        "No properties file found for {} with locale {} or default locale.", file, lang);
            }
        }

        return prop;
    }

    private static InputStream getResourceAsStream(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
