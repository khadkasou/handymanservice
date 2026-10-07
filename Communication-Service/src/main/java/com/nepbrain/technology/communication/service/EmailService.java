package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.record.EmailRecord;
import com.nepbrain.technology.core.exception.GlobalException;
import groovy.text.SimpleTemplateEngine;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Value("${mail.from}")
    private String emailFrom;

    private final SesV2Client sesV2Client;

    public void sendEmail(CommunicationRequest request, EmailRecord emailRecord) throws GlobalException {
        try {
            String subject = engine.createTemplate(emailRecord.subject()).make(request.getData()).toString();
            String htmlContent = engine.createTemplate(emailRecord.body()).make(request.getData()).toString();

            Content subjectContent = Content.builder().data(subject).charset("UTF-8").build();
            Content bodyContent = Content.builder().data(htmlContent).charset("UTF-8").build();
            Body body = Body.builder().html(bodyContent).build();
            Message message = Message.builder().subject(subjectContent).body(body).build();
            EmailContent emailContent = EmailContent.builder().simple(message).build();

            SendEmailRequest sendRequest = SendEmailRequest.builder()
                    .fromEmailAddress("GME Remittance <" + emailFrom + ">")
                    .destination(Destination.builder().toAddresses(request.getEmail()).build())
                    .content(emailContent)
                    .build();

            sesV2Client.sendEmail(sendRequest);
            logger.info("Message sent successfully to {} via AWS SES", request.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", request.getEmail(), e.getMessage());
            throw new GlobalException("EMAIL001", e);
        }
    }
}
