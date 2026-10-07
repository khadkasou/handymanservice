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
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTemplateCsvLoader {

    private final MessageRepository messageRepository;
    private final MessageDetailRepository messageDetailRepository;
    private final TransactionTemplate transactionTemplate;

    /**
     * Loads templates from a classpath CSV. Skips rows whose topic+language detail already exists.
     *
     * @return number of message detail rows inserted or updated
     */
    public int loadFromClasspath(String csvPath) {
        ClassPathResource resource = new ClassPathResource(csvPath);
        if (!resource.exists()) {
            log.warn("Message template CSV not found: {}. Skipping.", csvPath);
            return 0;
        }

        int detailsLoaded = 0;
        int skipped = 0;
        int failed = 0;
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
                try {
                    LoadRowResult result = transactionTemplate.execute(status -> loadRow(record));
                    if (result == LoadRowResult.INSERTED_OR_UPDATED) {
                        detailsLoaded++;
                    } else if (result == LoadRowResult.SKIPPED) {
                        skipped++;
                    }
                } catch (Exception ex) {
                    failed++;
                    String topic = getColumn(record, "topic");
                    log.warn("Skipped message template row for topic {}: {}", topic, ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to read message templates from {}", csvPath, e);
            return detailsLoaded;
        }

        log.info("Message template CSV {}: inserted/updated={}, skipped={}, failed={}",
                csvPath, detailsLoaded, skipped, failed);
        return detailsLoaded;
    }

    private LoadRowResult loadRow(CSVRecord record) {
        String topicStr = getColumn(record, "topic");
        String languageStr = getColumn(record, "language");
        String emailSubject = getColumn(record, "email_subject");
        String emailBody = getColumn(record, "email_body");
        if (topicStr == null || topicStr.isBlank() || emailSubject == null || emailBody == null) {
            return LoadRowResult.SKIPPED;
        }

        TopicEnum topic = parseTopic(topicStr);
        LanguageEnum language = parseLanguage(languageStr);

        MessageEntity message = messageRepository.findByTopic(topic);
        boolean enableEmail = parseBoolean(record, "enable_email", true);
        boolean enableSms = parseBoolean(record, "enable_sms", false);
        boolean enableNotification = parseBoolean(record, "enable_notification", true);

        if (message == null) {
            message = messageRepository.save(MessageEntity.builder()
                    .topic(topic)
                    .status(StatusEnum.ACTIVE)
                    .enableEmail(enableEmail)
                    .enableSms(enableSms)
                    .enableNotification(enableNotification)
                    .build());
        } else {
            Optional<MessageDetailEntity> existingDetail =
                    messageDetailRepository.findByMessageAndLanguage(message, language);
            if (existingDetail.isPresent()
                    && !detailChanged(existingDetail.get(), record, emailSubject, emailBody)) {
                return LoadRowResult.SKIPPED;
            }
            if (enableEmail != Boolean.TRUE.equals(message.getEnableEmail())
                    || enableSms != Boolean.TRUE.equals(message.getEnableSms())
                    || enableNotification != Boolean.TRUE.equals(message.getEnableNotification())) {
                message.setEnableEmail(enableEmail);
                message.setEnableSms(enableSms);
                message.setEnableNotification(enableNotification);
                message = messageRepository.save(message);
            }
        }

        Optional<MessageDetailEntity> existingDetail =
                messageDetailRepository.findByMessageAndLanguage(message, language);
        if (existingDetail.isPresent()) {
            return updateDetailIfChanged(existingDetail.get(), record, emailSubject, emailBody)
                    ? LoadRowResult.INSERTED_OR_UPDATED
                    : LoadRowResult.SKIPPED;
        }

        messageDetailRepository.save(MessageDetailEntity.builder()
                .message(message)
                .language(language)
                .emailSubject(emailSubject)
                .emailBody(emailBody)
                .smsBody(orEmpty(getColumn(record, "sms_body")))
                .notificationSubject(orEmpty(getColumn(record, "notification_subject")))
                .notificationBody(orEmpty(getColumn(record, "notification_body")))
                .build());
        return LoadRowResult.INSERTED_OR_UPDATED;
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

    private static boolean detailChanged(
            MessageDetailEntity detail,
            CSVRecord record,
            String emailSubject,
            String emailBody
    ) {
        String smsBody = orEmpty(getColumn(record, "sms_body"));
        String notificationSubject = orEmpty(getColumn(record, "notification_subject"));
        String notificationBody = orEmpty(getColumn(record, "notification_body"));
        return !emailSubject.equals(detail.getEmailSubject())
                || !emailBody.equals(detail.getEmailBody())
                || !smsBody.equals(orEmpty(detail.getSmsBody()))
                || !notificationSubject.equals(orEmpty(detail.getNotificationSubject()))
                || !notificationBody.equals(orEmpty(detail.getNotificationBody()));
    }

    private boolean updateDetailIfChanged(
            MessageDetailEntity detail,
            CSVRecord record,
            String emailSubject,
            String emailBody
    ) {
        if (!detailChanged(detail, record, emailSubject, emailBody)) {
            return false;
        }
        detail.setEmailSubject(emailSubject);
        detail.setEmailBody(emailBody);
        detail.setSmsBody(orEmpty(getColumn(record, "sms_body")));
        detail.setNotificationSubject(orEmpty(getColumn(record, "notification_subject")));
        detail.setNotificationBody(orEmpty(getColumn(record, "notification_body")));
        messageDetailRepository.save(detail);
        return true;
    }

    private enum LoadRowResult {
        INSERTED_OR_UPDATED,
        SKIPPED
    }
}
