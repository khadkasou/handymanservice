package com.nepbrain.technology.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageBundle {

    private MessageBundle() {
    }

    private static final Logger log = LoggerFactory.getLogger(MessageBundle.class);

    public static String getMessage(String key) {
        String errorProperties = "errors";
        try {
            Properties prop = readProperties(errorProperties + ".properties");
            return prop.getProperty(key);
        } catch (Exception e) {
            log.error("Error occurred while reading file {}", errorProperties, e);
        }
        return null;
    }

    public static String getMessage(String key, String file) {
        try {
            Properties prop = readProperties(file);
            return prop.getProperty(key);
        } catch (Exception e) {
            log.error("Error occurred while reading file " + file, e);
        }

        return null;
    }

    public static Properties readProperties(String fileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(fileName);

        Properties prop = new Properties();
        prop.load(input);

        return prop;
    }

    public static String getPropertyKey(String key, File file) {
        try {
            Properties prop = readFileAsProperty(file);
            return prop.getProperty(key);
        } catch (Exception e) {
            log.error("Error occurred while reading file " + file, e);
        }

        return null;
    }

    public static Properties readFileAsProperty(File filename) throws IOException {
        Properties prop;
        try (InputStream stream = new FileInputStream(filename)) {
            prop = new Properties();
            prop.load(stream);
        }
        return prop;
    }
}
