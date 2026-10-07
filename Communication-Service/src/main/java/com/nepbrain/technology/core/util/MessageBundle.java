package com.nepbrain.technology.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageBundle {

    private static final Logger logger = LoggerFactory.getLogger(MessageBundle.class);
    private static final String ERROR_PROPERTIES_BASE = "error";
    private static final String SUCCESS_PROPERTIES_BASE = "message";

    private MessageBundle() {
    }

    public static String getErrorMessage(String key) {
        return getMessage(key, ERROR_PROPERTIES_BASE);
    }
    public static String getSuccessMessage(String key) {
        return getMessage(key, SUCCESS_PROPERTIES_BASE);
    }

    private static String getMessage(String key, String file) {
        try {
            String lang = LocaleContextHolder.getLocale().getLanguage();
            Properties prop = loadPropertiesWithFallback(key,file, lang);
            return prop.getProperty(key);
        } catch (Exception e) {
            logger.error("Error occurred while reading file {}", file, e);
        }
        return null;
    }


    private static Properties loadPropertiesWithFallback(String key, String file, String lang)
            throws IOException {
        Properties prop = new Properties();

        String localeSpecificFileName = file + "_" + lang + ".properties";
        InputStream input = getResourceAsStream(localeSpecificFileName);

        if (input != null) {
            prop.load(input);
        }
        if (input == null || prop.getProperty(key) == null){
            // Fallback to the default locale (e.g., error-codes.properties or message.properties)
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


    public static String getMessageByCode(String code) {
        return getErrorMessage(code);
    }
}

