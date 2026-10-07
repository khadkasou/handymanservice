package com.nepbrain.technology.communication.init;

import com.nepbrain.technology.communication.entity.MessageDetailEntity;
import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.repository.MessageDetailRepository;
import com.nepbrain.technology.communication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTemplateCsvLoader {

    private final MessageRepository messageRepository;
    private final MessageDetailRepository messageDetailRepository;

    /**
     * Loads templates from a classpath CSV. Skips rows whose topic+language detail already exists.
     *
     * @return number of message detail rows inserted
     */
    public int loadFromClasspath(String csvPath) {
        ClassPathResource resource = new ClassPathResource(csvPath);
        if (!resource.exists()) {
            log.warn("Message template CSV not found: {}. Skipping.", csvPath);
            return 0;
        }

        int detailsLoaded = 0;
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .get()
                    .parse(reader);

            for (CSVRecord record : records) {
                if (loadRow(record)) {
                    detailsLoaded++;
                }
            }
        } catch (Exception e) {
            log.error("Failed to load message templates from {}", csvPath, e);
            throw new IllegalStateException("Failed to load message templates from " + csvPath, e);
        }
        return detailsLoaded;
    }

    private boolean loadRow(CSVRecord record) {
        String topicStr = getColumn(record, "topic");
        String languageStr = getColumn(record, "language");
        String emailSubject = getColumn(record, "email_subject");
        String emailBody = getColumn(record, "email_body");
        if (topicStr == null || topicStr.isBlank() || emailSubject == null || emailBody == null) {
            return false;
        }

        TopicEnum topic = parseTopic(topicStr);
        LanguageEnum language = parseLanguage(languageStr);

        MessageEntity message = messageRepository.findByTopic(topic);
        if (message == null) {
            message = MessageEntity.builder()
                    .topic(topic)
                    .status(StatusEnum.ACTIVE)
                    .enableEmail(parseBoolean(record, "enable_email", true))
                    .enableSms(parseBoolean(record, "enable_sms", false))
                    .enableNotification(parseBoolean(record, "enable_notification", true))
                    .build();
            message = messageRepository.save(message);
        }

        MessageDetailEntity existing = null;
        if (message.getMessages() != null) {
            existing = message.getMessages().stream()
                    .filter(d -> d.getLanguage() == language)
                    .findFirst()
                    .orElse(null);
        }
        if (existing != null) {
            boolean changed = !emailSubject.equals(existing.getEmailSubject())
                    || !emailBody.equals(existing.getEmailBody())
                    || !orEmpty(getColumn(record, "sms_body")).equals(orEmpty(existing.getSmsBody()))
                    || !orEmpty(getColumn(record, "notification_subject")).equals(orEmpty(existing.getNotificationSubject()))
                    || !orEmpty(getColumn(record, "notification_body")).equals(orEmpty(existing.getNotificationBody()));
            if (!changed) {
                return false;
            }
            existing.setEmailSubject(emailSubject);
            existing.setEmailBody(emailBody);
            existing.setSmsBody(orEmpty(getColumn(record, "sms_body")));
            existing.setNotificationSubject(orEmpty(getColumn(record, "notification_subject")));
            existing.setNotificationBody(orEmpty(getColumn(record, "notification_body")));
            messageDetailRepository.save(existing);
            return true;
        }

        MessageDetailEntity detail = MessageDetailEntity.builder()
                .message(message)
                .language(language)
                .emailSubject(emailSubject)
                .emailBody(emailBody)
                .smsBody(orEmpty(getColumn(record, "sms_body")))
                .notificationSubject(orEmpty(getColumn(record, "notification_subject")))
                .notificationBody(orEmpty(getColumn(record, "notification_body")))
                .build();
        messageDetailRepository.save(detail);
        return true;
    }

    private static String getColumn(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return (value != null && !value.isBlank()) ? value.trim() : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean parseBoolean(CSVRecord record, String column, boolean defaultValue) {
        String value = getColumn(record, column);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    private static String orEmpty(String value) {
        return value != null ? value : "";
    }

    private static TopicEnum parseTopic(String topic) {
        try {
            return TopicEnum.valueOf(topic.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown message topic in CSV: " + topic, e);
        }
    }

    private static LanguageEnum parseLanguage(String language) {
        if (language == null || language.isBlank()) {
            return LanguageEnum.EN;
        }
        try {
            return LanguageEnum.valueOf(language.trim().toUpperCase());
        } catch (Exception e) {
            return LanguageEnum.EN;
        }
    }
}
