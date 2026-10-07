package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.entity.CompanyEmailConfigEntity;
import com.nepbrain.technology.communication.enums.SettingDetailEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.record.EmailRecord;
import com.nepbrain.technology.communication.repository.SettingDetailRepository;
import com.nepbrain.technology.core.exception.GlobalException;
import groovy.text.SimpleTemplateEngine;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Outbound email:
 * 1) Company SMTP when enableEmail + complete SMTP config
 * 2) Else platform default: AWS SES (env or settings), then Mailgun SMTP from settings
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Value("${mail.from}")
    private String emailFrom;

    @Value("${mail.aws-access-key:}")
    private String awsAccessKey;

    @Value("${mail.aws-secret-key:}")
    private String awsSecretKey;

    @Value("${mail.aws-region:us-east-1}")
    private String awsRegion;

    private final CompanyMessagingChannelService companyMessagingChannelService;
    private final SettingDetailRepository settingDetailRepository;

    public void sendEmail(CommunicationRequest request, EmailRecord emailRecord) throws GlobalException {
        try {
            Map<String, String> data = request.getData() != null ? request.getData() : Map.of();
            String subject;
            String htmlContent;
            // Explicit HTML (company notices) must not go through Groovy — `$` / `${}` break rich text.
            if (data.isEmpty()) {
                subject = emailRecord.subject() != null ? emailRecord.subject() : "";
                htmlContent = emailRecord.body() != null ? emailRecord.body() : "";
            } else {
                subject = engine.createTemplate(emailRecord.subject()).make(data).toString();
                htmlContent = engine.createTemplate(emailRecord.body()).make(data).toString();
            }

            Optional<CompanyEmailConfigEntity> companyEmail =
                    companyMessagingChannelService.resolveCompanyEmailChannel(request.getCompanyId());
            if (companyEmail.isPresent()) {
                try {
                    sendViaSmtp(
                            companyEmail.get().getSmtpHost(),
                            companyEmail.get().getSmtpPort(),
                            companyEmail.get().getSmtpUsername(),
                            companyEmail.get().getSmtpPassword(),
                            companyEmail.get().getSmtpUseTls(),
                            companyEmail.get().getFromEmail(),
                            companyEmail.get().getFromName(),
                            request.getEmail(),
                            sanitizeEmailText(subject),
                            sanitizeEmailText(htmlContent),
                            "company SMTP");
                    return;
                } catch (Exception smtpEx) {
                    logger.warn(
                            "Company {} SMTP failed for {} — falling back to platform default: {}",
                            request.getCompanyId(),
                            request.getEmail(),
                            smtpEx.getMessage());
                }
            } else {
                logger.info(
                        "Company {} has no SMTP enabled — using platform default email for {}",
                        request.getCompanyId(),
                        request.getEmail());
            }

            sendViaPlatformDefault(request.getEmail(), sanitizeEmailText(subject), sanitizeEmailText(htmlContent));
        } catch (GlobalException ex) {
            throw ex;
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            logger.error("Failed to send email to {}: {}", request.getEmail(), detail);
            throw new GlobalException(
                    "EMAIL001",
                    "Failed to send email: " + detail,
                    HttpStatus.BAD_GATEWAY);
        }
    }

    private void sendViaPlatformDefault(String toEmail, String subject, String htmlContent) throws Exception {
        String accessKey = firstNonBlank(awsAccessKey, settingValue(SettingDetailEnum.EMAIL_AWS_ACCESS_KEY_ID));
        String secretKey = firstNonBlank(awsSecretKey, settingValue(SettingDetailEnum.EMAIL_AWS_SECRET_ACCESS_KEY));
        String region = firstNonBlank(settingValue(SettingDetailEnum.EMAIL_AWS_REGION), awsRegion, "us-east-1");
        String from = firstNonBlank(
                settingValue(SettingDetailEnum.EMAIL_AWS_MAIL_FROM),
                settingValue(SettingDetailEnum.EMAIL_FROM_EMAIL),
                emailFrom);

        Exception sesFailure = null;
        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            try {
                sendViaPlatformSes(toEmail, subject, htmlContent, from, accessKey, secretKey, region);
                return;
            } catch (Exception ex) {
                sesFailure = ex;
                logger.warn(
                        "Platform AWS SES failed for {} — trying Mailgun SMTP fallback: {}",
                        toEmail,
                        ex.getMessage());
            }
        }

        if (trySendViaMailgun(toEmail, subject, htmlContent)) {
            return;
        }

        if (sesFailure != null) {
            throw new GlobalException(
                    "EMAIL001",
                    "Failed to send email via SES and Mailgun is not configured: " + sesFailure.getMessage(),
                    HttpStatus.BAD_GATEWAY);
        }

        logger.error(
                "Platform default email is not configured (no AWS SES keys and no Mailgun SMTP settings) — cannot send to {}",
                toEmail);
        throw new GlobalException(
                "EMAIL001",
                "Email is not configured. Enable company SMTP, or configure platform AWS SES / Mailgun default settings.",
                HttpStatus.BAD_GATEWAY);
    }

    private boolean trySendViaMailgun(String toEmail, String subject, String htmlContent) throws Exception {
        String host = settingValue(SettingDetailEnum.EMAIL_MAILGUN_HOST);
        String portRaw = settingValue(SettingDetailEnum.EMAIL_MAILGUN_PORT);
        String username = settingValue(SettingDetailEnum.EMAIL_MAILGUN_USERNAME);
        String password = settingValue(SettingDetailEnum.EMAIL_MAILGUN_PASSWORD);
        String mailgunFrom = firstNonBlank(
                settingValue(SettingDetailEnum.EMAIL_FROM_EMAIL),
                settingValue(SettingDetailEnum.EMAIL_DEFAULT),
                emailFrom);

        if (!StringUtils.hasText(host) || !StringUtils.hasText(mailgunFrom)) {
            return false;
        }
        Integer port = parsePort(portRaw, 587);
        sendViaSmtp(
                host,
                port,
                username,
                password,
                true,
                mailgunFrom,
                settingValue(SettingDetailEnum.EMAIL_FROM_EMAIL_NAME),
                toEmail,
                subject,
                htmlContent,
                "platform Mailgun SMTP");
        return true;
    }

    private void sendViaSmtp(
            String smtpHost,
            Integer smtpPort,
            String smtpUsername,
            String smtpPassword,
            Boolean smtpUseTls,
            String fromEmail,
            String fromName,
            String toEmail,
            String subject,
            String htmlContent,
            String channelLabel) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort != null ? smtpPort : 587));
        boolean useTls = smtpUseTls == null || Boolean.TRUE.equals(smtpUseTls);
        props.put("mail.smtp.auth", StringUtils.hasText(smtpUsername) ? "true" : "false");
        props.put("mail.smtp.starttls.enable", useTls ? "true" : "false");
        if (smtpPort != null && smtpPort == 465) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        Session session;
        if (StringUtils.hasText(smtpUsername)) {
            String password = smtpPassword != null ? smtpPassword : "";
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, password);
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        MimeMessage message = new MimeMessage(session);
        String fromDisplay = StringUtils.hasText(fromName) ? fromName : fromEmail;
        message.setFrom(new InternetAddress(fromEmail, fromDisplay, "UTF-8"));
        message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject(subject, "UTF-8");
        message.setHeader("Content-Type", "text/html; charset=UTF-8");
        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
        logger.info("Message sent successfully to {} via {} ({})", toEmail, channelLabel, smtpHost);
    }

    /**
     * Prevent mojibake when a relay/client mishandles UTF-8 punctuation (en-dash, curly quotes, etc.).
     */
    static String sanitizeEmailText(String value) {
        if (value == null || value.isEmpty()) {
            return value == null ? "" : value;
        }
        return value
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"')
                .replace('\u00B7', '|')
                .replace('\u2022', '*')
                .replace('\u00A0', ' ');
    }

    private void sendViaPlatformSes(
            String toEmail,
            String subject,
            String htmlContent,
            String from,
            String accessKey,
            String secretKey,
            String region) {
        Content subjectContent = Content.builder().data(subject).charset("UTF-8").build();
        Content bodyContent = Content.builder().data(htmlContent).charset("UTF-8").build();
        Body body = Body.builder().html(bodyContent).build();
        Message message = Message.builder().subject(subjectContent).body(body).build();
        EmailContent emailContent = EmailContent.builder().simple(message).build();

        String fromAddress = StringUtils.hasText(from) ? from : emailFrom;
        SendEmailRequest sendRequest = SendEmailRequest.builder()
                .fromEmailAddress("HRM Suite <" + fromAddress + ">")
                .destination(Destination.builder().toAddresses(toEmail).build())
                .content(emailContent)
                .build();

        // Build SES client with resolved env/settings credentials (startup bean may have empty keys).
        try (SesV2Client client = SesV2Client.builder()
                .region(Region.of(StringUtils.hasText(region) ? region : "us-east-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {
            client.sendEmail(sendRequest);
        }
        logger.info("Message sent successfully to {} via platform AWS SES", toEmail);
    }

    private String settingValue(SettingDetailEnum setting) {
        if (setting == null) {
            return null;
        }
        return settingDetailRepository.findFirstByKeyIgnoreCase(setting.getCode())
                .map(detail -> detail.getValue())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .orElse(null);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static Integer parsePort(String raw, int defaultPort) {
        if (!StringUtils.hasText(raw)) {
            return defaultPort;
        }
        try {
            int port = Integer.parseInt(raw.trim());
            return port > 0 ? port : defaultPort;
        } catch (NumberFormatException ex) {
            return defaultPort;
        }
    }
}
